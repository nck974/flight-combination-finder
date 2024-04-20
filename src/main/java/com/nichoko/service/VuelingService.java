package com.nichoko.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
import com.nichoko.service.query.VuelingQueryService;
import com.nichoko.utils.DateUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;

import io.quarkus.cache.CacheResult;

@ApplicationScoped
@JBossLog
public class VuelingService implements AirlineService {

    static final String AIRLINE_NAME = "VUELING";

    @RestClient
    @Inject
    private VuelingQueryService vuelingQueryService;

    private List<Map<String, String>> buildGetCompanyFlightsParameters(FlightQueryDTO query) {
        List<Map<String, String>> parameters = new ArrayList<>();

        for (RouteCombination route : query.getRoutes()) {
            Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put("originCode", route.getOrigin());
            urlParameters.put("destinationCode", route.getDestination());
            urlParameters.put("currencyCode", "EUR");
            urlParameters.put("year", Integer.toString(query.getStartDate().getYear()));
            urlParameters.put("month", Integer.toString(query.getStartDate().getMonthValue()));
            urlParameters.put("monthsRange", Integer
                    .toString(query.getStartDate().getMonthValue() - query.getEndDate().getMonthValue()));

            parameters.add(urlParameters);
        }

        return parameters;
    }

    private List<Map<String, String>> buildGetFlightLandingDateQueryParameters(FlightQueryDTO query) {
        List<Map<String, String>> parameters = new ArrayList<>();

        for (RouteCombination route : query.getRoutes()) {

            // It seems this works different than the other method. A +1 is needed
            int monthRange = query.getStartDate().getMonthValue() - query.getEndDate().getMonthValue() + 1;

            Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put("departure", route.getOrigin());
            urlParameters.put("destination", route.getDestination());
            urlParameters.put("date", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(query.getStartDate()));
            urlParameters.put("monthsRange", Integer.toString(monthRange));

            parameters.add(urlParameters);
        }

        return parameters;
    }

    private DateTimeFormatter getFlightDatesFormatter() {
        return new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart()
                .appendLiteral('Z')
                .toFormatter();
    }

    private List<FlightDTO> toFlightDTO(Response response, Map<String, LocalDateTime> flightLandingDates) {
        JsonNode responseContent = response.readEntity(JsonNode.class);

        DateTimeFormatter dateFormatter = this.getFlightDatesFormatter();

        List<FlightDTO> flights = new ArrayList<>();
        if (responseContent.isArray()) {
            for (JsonNode flightResponse : responseContent) {
                FlightDTO flight = new FlightDTO();

                flight.setOrigin(flightResponse.get("DepartureStation").asText());
                flight.setDestination(flightResponse.get("ArrivalStation").asText());
                flight.setPrice((float) flightResponse.get("Price").asDouble());

                String departureDateString = flightResponse.get("DepartureDate").asText();
                LocalDateTime departureDateTime = LocalDateTime.parse(departureDateString, dateFormatter);
                flight.setDepartureDate(departureDateTime);

                // Landing Date form the response has the hour set to 0, therefore the
                // additional
                // response is used to get the flights
                String flightNumber = flightResponse.get("FlightID").asText();
                LocalDateTime landingDateTime;
                if (flightLandingDates.containsKey(flightNumber)) {
                    landingDateTime = flightLandingDates.get(flightNumber);
                } else {
                    String landingDateString = flightResponse.get("ArrivalDate").asText();
                    landingDateTime = LocalDateTime.parse(landingDateString, dateFormatter);
                }
                flight.setLandingDate(landingDateTime);

                int duration = DateUtils.calculateFlightDuration(departureDateTime, landingDateTime);
                flight.setDuration(duration);

                flights.add(flight);
            }
        } else {
            throw new ErrorFetchingDataException();
        }

        flights.sort(Comparator.comparing(FlightDTO::getDepartureDate));
        return flights;

    }

    private Map<String, LocalDateTime> processLandingDatesResponse(Response response) {
        JsonNode responseContent = response.readEntity(JsonNode.class);

        DateTimeFormatter landingDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm:ss");

        Map<String, LocalDateTime> flightLandingDates = new HashMap<>();
        if (responseContent.isArray()) {
            // Each month
            for (JsonNode yearMonthPair : responseContent) {

                JsonNode monthTimetables = yearMonthPair.get("FlightTimetables");

                if (monthTimetables.isArray()) {
                    // Each day
                    for (JsonNode dayTimetables : monthTimetables) {
                        String date = dayTimetables.get("Date").asText();

                        JsonNode dayFlights = dayTimetables.get("FlightSegments");
                        if (dayFlights.isArray()) {
                            // Each flight in date
                            for (JsonNode dayFlight : dayFlights) {
                                String flightNumber = dayFlight.get("FlightNumber").asText();
                                String arrivalTime = dayFlight.get("ArrivalTime").asText();

                                LocalDateTime landingDate = LocalDateTime.parse(date + arrivalTime,
                                        landingDateFormatter);
                                flightLandingDates.put(flightNumber, landingDate);
                            }
                        }

                    }

                }
            }
        } else {
            throw new ErrorFetchingDataException();
        }

        return flightLandingDates;

    }

    private List<ConnectionDTO> toConnectionDTO(Response response, ConnectionQueryDTO query) {
        JsonNode responseContent = response.readEntity(JsonNode.class);

        List<ConnectionDTO> connections = new ArrayList<>();

        Iterator<String> airports = responseContent.fieldNames();
        while (airports.hasNext()) {
            String airportCode = airports.next();

            if (!airportCode.equals(query.getOrigin())) {
                continue;
            }

            JsonNode airport = responseContent.get(airportCode);

            if (airport != null && airport.isArray()) {
                for (JsonNode connection : airport) {
                    String connectingAirport = connection.get("Connection").asText();

                    if (!connectingAirport.equals("")) {
                        continue;
                    }

                    String destinationCode = connection.get("DestinationCode").asText();
                    ConnectionDTO connectionDTO = new ConnectionDTO();
                    connectionDTO.setDestination(destinationCode);
                    connectionDTO.setOrigin(query.getOrigin());
                    connections.add(connectionDTO);
                }
            } else {
                throw new ErrorFetchingDataException();
            }

        }

        connections.sort(Comparator.comparing(ConnectionDTO::getDestination));
        return connections;

    }

    private List<FlightDTO> sendGetFlightsQuery(Map<String, String> parameters,
            Map<String, LocalDateTime> flightLandingDates) {
        List<FlightDTO> flights;

        log.debug("Fetching url with parameters: " + parameters);
        Response response;
        try {
            response = vuelingQueryService.getFlightsForDate(parameters);
        } catch (ClientWebApplicationException exception) {
            throw new ErrorFetchingDataException();
        }

        flights = toFlightDTO(response, flightLandingDates);

        return flights;
    }

    private Map<String, LocalDateTime> sendGetFlightsLandingDateQuery(Map<String, String> parameters) {

        log.debug("Fetching url with parameters: " + parameters);
        Response response;
        try {
            response = vuelingQueryService.getFlightsLandingDate(parameters);
        } catch (ClientWebApplicationException exception) {
            throw new ErrorFetchingDataException();
        }

        return processLandingDatesResponse(response);
    }

    private List<ConnectionDTO> sendGetConnectionsQuery(ConnectionQueryDTO query) {

        List<ConnectionDTO> connections;
        Response response;
        try {
            response = vuelingQueryService.getAllAirportConnections();
        } catch (ClientWebApplicationException exception) {
            throw new ErrorFetchingDataException();
        }

        connections = toConnectionDTO(response, query);

        return connections;
    }

    private Map<String, LocalDateTime> getFlightsLandingDates(FlightQueryDTO query) {
        List<Map<String, String>> parametersSet = this.buildGetFlightLandingDateQueryParameters(query);
        Map<String, LocalDateTime> flightLandingDates = new HashMap<>();
        for (Map<String, String> parameters : parametersSet) {
            flightLandingDates.putAll(this.sendGetFlightsLandingDateQuery(parameters));
        }

        return flightLandingDates;
    }

    @Override
    @CacheResult(cacheName = "flightsForDateVueling")
    public List<FlightDTO> getCompanyFlights(FlightQueryDTO query) {
        Map<String, LocalDateTime> flightLandingDates = this.getFlightsLandingDates(query);
        List<Map<String, String>> parametersSet = buildGetCompanyFlightsParameters(query);

        List<FlightDTO> flights = new ArrayList<>();
        for (Map<String, String> parameters : parametersSet) {
            flights.addAll(this.sendGetFlightsQuery(parameters, flightLandingDates));
        }

        return flights;

    }

    @Override
    @CacheResult(cacheName = "airportConnection")
    public List<ConnectionDTO> getAirportConnections(ConnectionQueryDTO query) {
        return this.sendGetConnectionsQuery(query);
    }

    @Override
    public String getAirlineName() {
        return VuelingService.AIRLINE_NAME;
    }

}
