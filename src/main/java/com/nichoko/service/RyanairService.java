package com.nichoko.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.fasterxml.jackson.databind.JsonNode;
import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.query.ConnectionQueryDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO.RouteCombination;
import com.nichoko.exception.ErrorFetchingDataException;
import com.nichoko.service.interfaces.AirlineService;
import com.nichoko.service.query.RyanairQueryService;
import com.nichoko.utils.DateUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;

import io.quarkus.cache.CacheResult;

@ApplicationScoped
@JBossLog
public class RyanairService implements AirlineService {

    static final String AIRLINE_NAME = "RYANAIR";

    @RestClient
    @Inject
    private RyanairQueryService ryanairQueryService;

    private List<Map<String, String>> buildGetCompanyFlightsParameters(FlightQueryDTO query) {
        List<Map<String, String>> parameters = new ArrayList<>();

        for (RouteCombination route : query.getRoutes()) {
            Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put("departureAirportIataCode", route.getOrigin());
            urlParameters.put("outboundDepartureDateFrom", query.getStartDate().toString());
            urlParameters.put("market", "en-gb");
            urlParameters.put("adultPaxCount", "1");
            urlParameters.put("arrivalAirportIataCode", route.getDestination());
            urlParameters.put("searchMode", "ALL");
            urlParameters.put("outboundDepartureDateTo", query.getEndDate().toString());
            urlParameters.put("inboundDepartureDateFrom", query.getStartDate().toString());
            urlParameters.put("inboundDepartureDateTo", query.getEndDate().toString());
            urlParameters.put("durationFrom", "1");
            urlParameters.put("durationTo", "7");
            urlParameters.put("outboundDepartureTimeFrom", "00:00");
            urlParameters.put("outboundDepartureTimeTo", "23:59");
            urlParameters.put("inboundDepartureTimeFrom", "00:00");
            urlParameters.put("inboundDepartureTimeTo", "23:59");

            parameters.add(urlParameters);
        }

        return parameters;
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
                LocalDateTime departureDateTime = LocalDateTime.parse(departureDateString,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                flight.setDepartureDate(departureDateTime);

                String landingDateString = outbound.get("arrivalDate").asText();
                LocalDateTime landingDateTime = LocalDateTime.parse(landingDateString,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                flight.setLandingDate(landingDateTime);
                int duration = DateUtils.calculateFlightDuration(departureDateTime, landingDateTime);
                flight.setDuration(duration);

                flight.setAirlineName(AIRLINE_NAME);

                flights.add(flight);
            }
        } else {
            throw new ErrorFetchingDataException(new RuntimeException("Unexpected content"));
        }

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
            throw new ErrorFetchingDataException(new RuntimeException("Unexpected content"));
        }

        connections.sort(Comparator.comparing(ConnectionDTO::getDestination));
        return connections;

    }

    private List<FlightDTO> sendGetFlightsQuery(Map<String, String> parameters) {
        List<FlightDTO> flights;

        log.debug("Fetching url with parameters: " + parameters);
        Response response;
        try {
            response = ryanairQueryService.getFlightsForDate(parameters);
        } catch (ClientWebApplicationException exception) {
            throw new ErrorFetchingDataException(exception);
        }

        flights = toFlightDTO(response);

        return flights;
    }

    private List<ConnectionDTO> sendGetConnectionsQuery(String iataCode, ConnectionQueryDTO query) {

        List<ConnectionDTO> connections;
        Response response;
        try {
            response = ryanairQueryService.getAirportConnections(iataCode);
        } catch (ClientWebApplicationException exception) {
            throw new ErrorFetchingDataException(exception);
        }

        connections = toConnectionDTO(response, query);

        return connections;
    }

    @Override
    @CacheResult(cacheName = "flightsForDateRyanair")
    public List<FlightDTO> getCompanyFlights(FlightQueryDTO query) {
        List<Map<String, String>> parametersSet = buildGetCompanyFlightsParameters(query);

        List<FlightDTO> flights = new ArrayList<>();
        for (Map<String, String> parameters : parametersSet) {
            flights.addAll(this.sendGetFlightsQuery(parameters));
        }

        return flights;

    }

    @Override
    @CacheResult(cacheName = "airportConnectionRyanair")
    public List<ConnectionDTO> getAirportConnections(ConnectionQueryDTO query) {
        return this.sendGetConnectionsQuery(query.getOrigin(), query);
    }

    @Override
    public String getAirlineName() {
        return RyanairService.AIRLINE_NAME;
    }
}
