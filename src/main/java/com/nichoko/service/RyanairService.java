package com.nichoko.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightQueryDTO;
import com.nichoko.exception.ErrorFetchingDataException;
import com.nichoko.service.interfaces.AirlineService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class RyanairService implements AirlineService {

    private Logger logger = Logger.getLogger(RyanairService.class);

    private String buildURL(FlightQueryDTO query) {
        StringBuilder urlBuilder = new StringBuilder("https://www.ryanair.com/api/farfnd/v4/roundTripFares?");
        urlBuilder.append("departureAirportIataCode=").append(query.getOrigin())
                .append("&outboundDepartureDateFrom=").append(query.getStartDate())
                .append("&market=").append("en-gb") // Change to de?
                .append("&adultPaxCount=").append(1)
                .append("&arrivalAirportIataCode=").append(query.getDestination())
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

        return urlBuilder.toString();
    }

    List<FlightDTO> toFlightDTO(Response response) {
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

    @Override
    public List<FlightDTO> getCompanyFlights(FlightQueryDTO query) {
        String url = buildURL(query);

        Client client = ClientBuilder.newClient();

        List<FlightDTO> flights;
        try {
            logger.debug("Fetching url:\n" + url);
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

}
