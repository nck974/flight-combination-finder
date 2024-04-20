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
import java.util.stream.Collectors;

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
                    .toString(query.getEndDate().getMonthValue() - query.getStartDate().getMonthValue()));

            parameters.add(urlParameters);
        }

        return parameters;
    }

    private List<Map<String, String>> buildGetFlightLandingDateQueryParameters(FlightQueryDTO query) {
        List<Map<String, String>> parameters = new ArrayList<>();

        for (RouteCombination route : query.getRoutes()) {

            // It seems this works different than the other method. A +1 is needed
            int monthRange = query.getEndDate().getMonthValue() - query.getStartDate().getMonthValue() + 1;

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

    /**
     * Landing Date form the response has the hour set to 0, therefore the
     * additional query is used
     * to get the flights
     * 
     * @param flightLandingDates
     * @param dateFormatter
     * @param flightResponse
     * @param departureDateTime
     * @return
     */
    private LocalDateTime extractLandingDate(Map<String, LocalDateTime> flightLandingDates,
            DateTimeFormatter dateFormatter, JsonNode flightResponse, LocalDateTime departureDateTime) {
        String flightNumber = flightResponse.get("FlightID").asText();

        LocalDateTime landingDateTime;
        String landingDateString = flightResponse.get("ArrivalDate").asText();
        landingDateTime = LocalDateTime.parse(landingDateString, dateFormatter);

        String landingDateKey = landingDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyy"))
                + flightNumber;
        if (flightLandingDates.containsKey(landingDateKey)) {
            landingDateTime = flightLandingDates.get(landingDateKey);

            // If a flight lands after 12 add that day because it can not be read form the
            // times schedule response
            if (landingDateTime.isBefore(departureDateTime)) {
                landingDateTime = landingDateTime.plusDays(1);
            }
        }
        return landingDateTime;
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

                LocalDateTime landingDateTime;

                landingDateTime = this.extractLandingDate(flightLandingDates, dateFormatter, flightResponse,
                        departureDateTime);
                flight.setLandingDate(landingDateTime);

                int duration = DateUtils.calculateFlightDuration(departureDateTime, landingDateTime);
                flight.setDuration(duration);

                flight.setAirlineName(AIRLINE_NAME);

                flights.add(flight);
            }
        } else {
            throw new ErrorFetchingDataException(new RuntimeException("Unexpected content toFlightDTO"));
        }

        flights.sort(Comparator.comparing(FlightDTO::getDepartureDate));
        return flights;

    }

    private void extractDayFlights(DateTimeFormatter landingDateFormatter,
            Map<String, LocalDateTime> flightLandingDates,
            JsonNode dayTimetables) {
        String date = dayTimetables.get("Date").asText();

        JsonNode dayFlights = dayTimetables.get("FlightSegments");
        if (dayFlights.isArray()) {
            // Each flight in date
            for (JsonNode dayFlight : dayFlights) {
                String flightNumber = dayFlight.get("FlightNumber").asText();
                String arrivalTime = dayFlight.get("ArrivalTime").asText();

                LocalDateTime landingDate = LocalDateTime.parse(date + arrivalTime,
                        landingDateFormatter);

                // Key is date + flight number. As the same flight number is used
                // many times in different days
                flightLandingDates.put(date + flightNumber, landingDate);
            }
        }
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
                        this.extractDayFlights(landingDateFormatter, flightLandingDates, dayTimetables);
                    }

                }
            }
        } else {
            throw new ErrorFetchingDataException(new RuntimeException("Unexpected content"));
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

                    // If there is a connecting airport it means is not a direct connection
                    // so it has to be filtered out
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
                throw new ErrorFetchingDataException(new RuntimeException("Unexpected content"));
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
            throw new ErrorFetchingDataException(exception);
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
            throw new ErrorFetchingDataException(exception);
        }

        return processLandingDatesResponse(response);
    }

    private List<ConnectionDTO> sendGetConnectionsQuery(ConnectionQueryDTO query) {

        List<ConnectionDTO> connections;
        Response response;
        try {
            response = vuelingQueryService.getAllAirportConnections();
        } catch (ClientWebApplicationException exception) {
            throw new ErrorFetchingDataException(exception);
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

    /**
     * This method is necessary because vueling returns flights through the API when
     * there are no direct connections. So first through another endpoint is
     * possible to find connections and then filter here only to search those known
     * direct connections
     * 
     * @param query
     * @return
     */
    private FlightQueryDTO getFilteredQuery(FlightQueryDTO query) {
        FlightQueryDTO filteredQuery = new FlightQueryDTO();
        filteredQuery.setStartDate(query.getStartDate());
        filteredQuery.setEndDate(query.getEndDate());

        List<RouteCombination> routes = new ArrayList<>();
        for (RouteCombination combination : query.getRoutes()) {
            List<ConnectionDTO> connections = this
                    .getAirportConnections(new ConnectionQueryDTO(combination.getOrigin()));
            for (ConnectionDTO connection : connections) {
                if (connection.getDestination().equals(combination.getDestination())) {
                    routes.add(combination);
                    break;
                }
            }
        }

        filteredQuery.setRoutes(routes);

        return filteredQuery;
    }

    @Override
    @CacheResult(cacheName = "flightsForDateVueling")
    public List<FlightDTO> getCompanyFlights(FlightQueryDTO query) {
        FlightQueryDTO filteredQuery = this.getFilteredQuery(query);

        Map<String, LocalDateTime> flightLandingDates = this.getFlightsLandingDates(filteredQuery);
        List<Map<String, String>> parametersSet = buildGetCompanyFlightsParameters(filteredQuery);

        List<FlightDTO> flights = new ArrayList<>();
        for (Map<String, String> parameters : parametersSet) {

            List<FlightDTO> queryFlights = this.sendGetFlightsQuery(parameters, flightLandingDates);

            // Results are by month si a filtering has to be done
            List<FlightDTO> filteredFlights = queryFlights.stream()
                    .filter(flight -> flight.getDepartureDate().toLocalDate().isBefore(filteredQuery.getEndDate()))
                    .collect(Collectors.toList());

            flights.addAll(filteredFlights);
        }

        return flights;

    }

    @Override
    @CacheResult(cacheName = "airportConnectionVueling")
    public List<ConnectionDTO> getAirportConnections(ConnectionQueryDTO query) {
        return this.sendGetConnectionsQuery(query);
    }

    @Override
    public String getAirlineName() {
        return VuelingService.AIRLINE_NAME;
    }

}
