package com.nichoko.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightQueryDTO;
import com.nichoko.domain.dto.FlightRouteDTO;
import com.nichoko.domain.dto.FlightQueryDTO.RouteCombination;
import com.nichoko.service.interfaces.FlightsDetailsService;
import com.nichoko.utils.CartesianProduct;
import com.nichoko.utils.DateUtils;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlightDetailsServiceImpl implements FlightsDetailsService {

    /**
     * Structure the data in a map organized by date and route to have quick access
     * to
     * the flight combinations.
     * 
     * @param flights
     * @return
     */
    private Map<LocalDate, Map<String, List<FlightDTO>>> getStructuredData(List<FlightDTO> flights) {

        Map<LocalDate, Map<String, List<FlightDTO>>> sortedFlightData = new HashMap<>();

        for (FlightDTO flight : flights) {
            LocalDate departureDate = flight.getDepartureDate().toLocalDate();
            String route = flight.getOrigin() + "-" + flight.getDestination();

            sortedFlightData.computeIfAbsent(departureDate, k -> new HashMap<>());

            if (!sortedFlightData.get(departureDate).containsKey(route)) {
                sortedFlightData.get(departureDate).put(route, new ArrayList<>());
            }
            sortedFlightData.get(departureDate).get(route).add(flight);
        }

        return sortedFlightData;
    }

    /**
     * Check if for the given day, all routes of the itinerary have at least one
     * flight.
     * 
     * @param dayRoutes
     * @param routes
     * @return
     */
    private boolean isRouteMissingInItinerary(Map<String, List<FlightDTO>> dayFlights, List<RouteCombination> routes) {
        for (RouteCombination route : routes) {
            String routeName = route.getRouteName();
            if (!dayFlights.containsKey(routeName) || dayFlights.get(routeName).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that every flight of the list departs after the previous one has landed
     * 
     * @param combination
     * @return
     */
    private boolean isValidCombination(List<FlightDTO> combination) {
        if (combination.size() == 1) {
            return true;
        }

        for (int i = 1; i < combination.size(); i++) {
            if (combination.get(i).getDepartureDate()
                    .isBefore(combination.get(i - 1).getLandingDate())) {
                return false;
            }
        }

        return true;
    }

    private float getTotalPrice(List<FlightDTO> flights) {
        return (float) flights.stream()
                .mapToDouble(FlightDTO::getPrice)
                .sum();
    }

    /**
     * Filter all combinations that can not be taken because one flight departs
     * before the other
     * one has landed
     * 
     * @param combinations
     * @return
     */
    private List<List<FlightDTO>> filterValidCombinations(List<List<FlightDTO>> combinations) {
        List<List<FlightDTO>> validCombinations = new ArrayList<>();
        for (List<FlightDTO> combination : combinations) {
            if (!this.isValidCombination(combination)) {
                continue;
            }
            validCombinations.add(combination);
        }
        return validCombinations;
    }

    /**
     * For each day in the range of the query check if there is a possible
     * combination of all flights
     * that would allow to get from the origin to the destination going through all
     * routes without
     * overlapping the flight times
     * 
     * @param query
     * @param flights
     * @return routes
     */
    @Override
    public List<FlightRouteDTO> getItineraryOptions(FlightQueryDTO query, List<FlightDTO> flights) {

        List<FlightRouteDTO> itineraryOptions = new ArrayList<>();

        // Create structure to make filtering easier
        Map<LocalDate, Map<String, List<FlightDTO>>> sortedFlightData = this.getStructuredData(flights);

        for (LocalDate date : DateUtils.getDatesRange(query.getStartDate(), query.getEndDate())) {

            if (!sortedFlightData.containsKey(date)
                    || isRouteMissingInItinerary(sortedFlightData.get(date), query.getRoutes())) {
                continue;
            }

            // Build lists with each available option of the itinerary
            List<List<FlightDTO>> flightLists = new ArrayList<>();
            for (RouteCombination route : query.getRoutes()) {
                String routeName = route.getOrigin() + "-" + route.getDestination();
                flightLists.add(sortedFlightData.get(date).get(routeName));
            }

            // Generate combinations
            List<List<FlightDTO>> combinations = CartesianProduct.generateListsCartesianProduct(flightLists);

            // Filter combinations
            List<List<FlightDTO>> validCombinations = filterValidCombinations(combinations);

            // Build final object with the valid routes
            for (List<FlightDTO> combination : validCombinations) {
                FlightRouteDTO route = new FlightRouteDTO();
                route.setPrice(this.getTotalPrice(combination));
                route.setDepartureDate(combination.get(0).getDepartureDate());
                route.setLandingDate(combination.get(combination.size() - 1).getLandingDate());
                route.setDuration(DateUtils.calculateFlightDuration(route.getDepartureDate(), route.getLandingDate()));
                itineraryOptions.add(route);
            }
        }

        return itineraryOptions;
    }

    /**
     * Fill the duration of each flight with an integer number. This method takes
     * into account
     * flights that have multiple hours, also it returns always one hour
     * 
     * @param query
     * @param flights
     * @return routes
     */
    @Override
    public List<FlightDTO> setFlightsDuration(List<FlightDTO> flights) {
        for (FlightDTO flight : flights) {
            LocalDateTime departureDateTime = flight.getDepartureDate();
            LocalDateTime landingDateTime = flight.getLandingDate();
            flight.setDuration(DateUtils.calculateFlightDuration(departureDateTime, landingDateTime));
        }
        return flights;
    }

}
