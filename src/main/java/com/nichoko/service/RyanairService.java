package com.nichoko.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.JsonNode;
import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.ConnectionQueryDTO;
import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightQueryDTO;
import com.nichoko.domain.dto.FlightQueryDTO.RouteCombination;
import com.nichoko.exception.ErrorFetchingDataException;
import com.nichoko.service.interfaces.AirlineService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@JBossLog
public class RyanairService implements AirlineService {

    @ConfigProperty(name = "flights.airlines.ryanair-schedules")
    private String flightsUrl;

    @ConfigProperty(name = "flights.airlines.airport-connections")
    private String airportConnectionsUrl;

    private List<String> buildGetCompanyFlightsUrls(FlightQueryDTO query) {
        List<String> urls = new ArrayList<>();

        for (RouteCombination route : query.getRoutes()) {
            StringBuilder urlBuilder = new StringBuilder(flightsUrl + "?");
            urlBuilder.append("departureAirportIataCode=").append(route.getOrigin())
                    .append("&outboundDepartureDateFrom=").append(query.getStartDate())
                    .append("&market=").append("en-gb") // Change to de?
                    .append("&adultPaxCount=").append(1)
                    .append("&arrivalAirportIataCode=").append(route.getDestination())
                    .append("&searchMode=").append("ALL")
                    .append("&outboundDepartureDateTo=").append(query.getEndDate())
                    .append("&inboundDepartureDateFrom=").append(query.getStartDate())
                    .append("&inboundDepartureDateTo=").append(query.getEndDate())
                    .append("&durationFrom=").append(1)
                    .append("&durationTo=").append(7)
                    .append("&outboundDepartureTimeFrom=").append("00:00")
                    .append("&outboundDepartureTimeTo=").append("23:59")
                    .append("&inboundDepartureTimeFrom=").append("00:00")
                    .append("&inboundDepartureTimeTo=").append("23:59");
            urls.add(urlBuilder.toString());
        }

        return urls;
    }

    private String buildGetAirportConnections(ConnectionQueryDTO query) {
        StringBuilder urlBuilder = new StringBuilder(airportConnectionsUrl + "/");
        urlBuilder.append(query.getOrigin());
        return urlBuilder.toString();
    }

    private List<FlightDTO> toFlightDTO(Response response) {
        JsonNode responseContent = response.readEntity(JsonNode.class);
        JsonNode fares = responseContent.get("fares");

        List<FlightDTO> flights = new ArrayList<>();
        if (fares.isArray()) {
            for (JsonNode fare : fares) {
                FlightDTO flight = new FlightDTO();

                JsonNode outbound = fare.get("outbound");
                JsonNode departureAirport = outbound.get("departureAirport");
                JsonNode arrivalAirport = outbound.get("arrivalAirport");
                JsonNode price = outbound.get("price");

                flight.setOrigin(departureAirport.get("iataCode").asText());
                flight.setDestination(arrivalAirport.get("iataCode").asText());
                flight.setPrice((float) price.get("value").asDouble());

                String departureDateString = outbound.get("departureDate").asText();
                LocalDateTime departureDate = LocalDateTime.parse(departureDateString,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                flight.setDepartureDate(departureDate);

                String landingDateString = outbound.get("arrivalDate").asText();
                LocalDateTime landingDate = LocalDateTime.parse(landingDateString,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                flight.setLandingDate(landingDate);

                flights.add(flight);
            }
        } else {
            throw new ErrorFetchingDataException("No flights found", 400);
        }

        flights.sort(Comparator.comparing(FlightDTO::getDepartureDate));
        return flights;

    }

    private List<ConnectionDTO> toConnectionDTO(Response response, ConnectionQueryDTO query) {
        JsonNode responseContent = response.readEntity(JsonNode.class);

        List<ConnectionDTO> connections = new ArrayList<>();
        if (responseContent.isArray()) {
            for (JsonNode connectionNode : responseContent) {
                ConnectionDTO connection = new ConnectionDTO();
                JsonNode arrivalAirport = connectionNode.get("arrivalAirport");
                connection.setDestination(arrivalAirport.get("code").asText());
                connection.setOrigin(query.getOrigin());
                connections.add(connection);
            }
        } else {
            throw new ErrorFetchingDataException("No connections found", 404);
        }

        connections.sort(Comparator.comparing(ConnectionDTO::getDestination));
        return connections;

    }

    private List<FlightDTO> sendGetFlightsQuery(String url) {
        Client client = ClientBuilder.newClient();

        List<FlightDTO> flights;
        try {
            log.info("Fetching url:\n" + url);
            Response response = client.target(url).request().get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                flights = toFlightDTO(response);
            } else {
                throw new ErrorFetchingDataException(
                        "Failed to fetch data from Ryanair API. Status code: " + response.getStatus(),
                        response.getStatus());
            }
        } finally {
            client.close();
        }
        return flights;
    }

    private List<ConnectionDTO> sendGetConnectionsQuery(String url, ConnectionQueryDTO query) {
        Client client = ClientBuilder.newClient();

        List<ConnectionDTO> flights;
        try {
            log.info("Fetching url:\n" + url);
            Response response = client.target(url).request().get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                flights = toConnectionDTO(response, query);
            } else {
                throw new ErrorFetchingDataException(
                        "Failed to fetch data from Ryanair API. Status code: " + response.getStatus(),
                        response.getStatus());
            }
        } finally {
            client.close();
        }
        return flights;
    }

    @Override
    public List<FlightDTO> getCompanyFlights(FlightQueryDTO query) {
        List<String> urls = buildGetCompanyFlightsUrls(query);

        List<FlightDTO> flights = new ArrayList<>();
        for (String url : urls) {
            flights.addAll(this.sendGetFlightsQuery(url));
        }
        return flights;

    }

    @Override
    public List<ConnectionDTO> getAirportConnections(ConnectionQueryDTO query) {
        String url = buildGetAirportConnections(query);
        return this.sendGetConnectionsQuery(url, query);
    }

}
