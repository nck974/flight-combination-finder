package com.nichoko.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightRouteDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO.RouteCombination;
import com.nichoko.service.interfaces.FlightsRouteService;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class FlightRouteServiceImplTest {

    @Inject
    private FlightsRouteService flightsRouteService;

    private FlightQueryDTO getMockQueryTwoConnections() {
        FlightQueryDTO queryDTO = new FlightQueryDTO();

        RouteCombination route1 = new RouteCombination("SDR", "OVD");
        RouteCombination route2 = new RouteCombination("OVD", "STN");
        queryDTO.setRoutes(List.of(route1, route2));
        queryDTO.setStartDate(LocalDate.of(2024, 3, 10));
        queryDTO.setEndDate(LocalDate.of(2024, 4, 30));
        return queryDTO;
    }

    private FlightQueryDTO getMockQueryDirectFlight() {
        FlightQueryDTO queryDTO = new FlightQueryDTO();

        RouteCombination route1 = new RouteCombination("SDR", "OVD");
        queryDTO.setRoutes(List.of(route1));
        queryDTO.setStartDate(LocalDate.of(2024, 3, 10));
        queryDTO.setEndDate(LocalDate.of(2024, 4, 30));
        return queryDTO;
    }

    @Test
    void testGetAvailableRoutes__twoConnections() {
        FlightQueryDTO queryDTO = this.getMockQueryTwoConnections();

        // Mock flight data
        FlightDTO flight0 = new FlightDTO();
        flight0.setOrigin("SDR");
        flight0.setDestination("OVD");
        flight0.setDepartureDate(LocalDateTime.of(2024, 4, 1, 12, 00));
        flight0.setLandingDate(LocalDateTime.of(2024, 4, 1, 10, 00));
        flight0.setPrice(10F);        
        

        FlightDTO flight1 = new FlightDTO();
        flight1.setOrigin("SDR");
        flight1.setDestination("OVD");
        flight1.setDepartureDate(LocalDateTime.of(2024, 4, 1, 13, 00));
        flight1.setLandingDate(LocalDateTime.of(2024, 4, 1, 14, 00));
        flight1.setPrice(10F);

        FlightDTO flight2 = new FlightDTO();
        flight2.setOrigin("OVD");
        flight2.setDestination("STN");
        flight2.setDepartureDate(LocalDateTime.of(2024, 4, 1, 15, 00));
        flight2.setLandingDate(LocalDateTime.of(2024, 4, 1, 16, 00));
        flight2.setPrice(20F);

        FlightDTO flight3 = new FlightDTO();
        flight3.setOrigin("SDR");
        flight3.setDestination("OVD");
        flight3.setDepartureDate(LocalDateTime.of(2024, 4, 2, 15, 00));
        flight3.setLandingDate(LocalDateTime.of(2024, 4, 2, 16, 00));
        flight3.setPrice(40F);

        List<FlightDTO> flights = Arrays.asList(flight0, flight1, flight2, flight3);

        // Call the method under test
        List<FlightRouteDTO> availableRoutes = flightsRouteService.getAvailableRoutes(queryDTO, flights);

        // Assertions
        assertNotNull(availableRoutes);
        assertEquals(2, availableRoutes.size());
        FlightRouteDTO route = availableRoutes.get(1);
        assertEquals(30F, route.getPrice());
        assertEquals(LocalDateTime.of(2024, 4, 1, 13, 00), route.getDepartureDate());
        assertEquals(LocalDateTime.of(2024, 4, 1, 16, 00), route.getLandingDate());
        assertEquals(3, route.getDuration());
    }

    @Test
    void testGetAvailableRoutes__firstFlightBeforeSecond() {
        FlightQueryDTO queryDTO = this.getMockQueryTwoConnections();

        // Mock flight data
        FlightDTO flight1 = new FlightDTO();
        flight1.setOrigin("SDR");
        flight1.setDestination("OVD");
        flight1.setDepartureDate(LocalDateTime.of(2024, 4, 1, 15, 00));
        flight1.setLandingDate(LocalDateTime.of(2024, 4, 1, 16, 00));
        flight1.setPrice(10F);

        FlightDTO flight2 = new FlightDTO();
        flight2.setOrigin("OVD");
        flight2.setDestination("STN");
        flight2.setDepartureDate(LocalDateTime.of(2024, 4, 1, 13, 00));
        flight2.setLandingDate(LocalDateTime.of(2024, 4, 1, 14, 00));
        flight2.setPrice(20F);

        FlightDTO flight3 = new FlightDTO();
        flight3.setOrigin("SDR");
        flight3.setDestination("OVD");
        flight3.setDepartureDate(LocalDateTime.of(2024, 4, 2, 15, 00));
        flight3.setLandingDate(LocalDateTime.of(2024, 4, 2, 16, 00));
        flight3.setPrice(40F);

        List<FlightDTO> flights = Arrays.asList(flight1, flight2, flight3);

        // Call the method under test
        List<FlightRouteDTO> availableRoutes = flightsRouteService.getAvailableRoutes(queryDTO, flights);

        // Assertions
        assertNotNull(availableRoutes);
        assertEquals(0, availableRoutes.size());
    }

    @Test
    void testGetAvailableRoutes__routeMissingInItinerary() {
        FlightQueryDTO queryDTO = this.getMockQueryTwoConnections();

        // Mock flight data
        FlightDTO flight1 = new FlightDTO();
        flight1.setOrigin("SDR");
        flight1.setDestination("OVD");
        flight1.setDepartureDate(LocalDateTime.of(2024, 4, 1, 15, 00));
        flight1.setLandingDate(LocalDateTime.of(2024, 4, 1, 16, 00));
        flight1.setPrice(10F);

        FlightDTO flight2 = new FlightDTO();
        flight2.setOrigin("SDR");
        flight2.setDestination("OVD");
        flight2.setDepartureDate(LocalDateTime.of(2024, 4, 1, 13, 00));
        flight2.setLandingDate(LocalDateTime.of(2024, 4, 1, 14, 00));
        flight2.setPrice(20F);

        FlightDTO flight3 = new FlightDTO();
        flight3.setOrigin("SDR");
        flight3.setDestination("OVD");
        flight3.setDepartureDate(LocalDateTime.of(2024, 4, 2, 15, 00));
        flight3.setLandingDate(LocalDateTime.of(2024, 4, 2, 16, 00));
        flight3.setPrice(40F);

        List<FlightDTO> flights = Arrays.asList(flight1, flight2, flight3);

        // Call the method under test
        List<FlightRouteDTO> availableRoutes = flightsRouteService.getAvailableRoutes(queryDTO, flights);

        // Assertions
        assertNotNull(availableRoutes);
        assertEquals(0, availableRoutes.size());
    }
    @Test
    void testGetAvailableRoutes__directFlight() {
        FlightQueryDTO queryDTO = this.getMockQueryDirectFlight();

        // Mock flight data
        FlightDTO flight1 = new FlightDTO();
        flight1.setOrigin("SDR");
        flight1.setDestination("OVD");
        flight1.setDepartureDate(LocalDateTime.of(2024, 4, 1, 13, 00));
        flight1.setLandingDate(LocalDateTime.of(2024, 4, 1, 14, 00));
        flight1.setPrice(10F);

        FlightDTO flight2 = new FlightDTO();
        flight2.setOrigin("SDR");
        flight2.setDestination("OVD");
        flight2.setDepartureDate(LocalDateTime.of(2024, 4, 2, 15, 00));
        flight2.setLandingDate(LocalDateTime.of(2024, 4, 2, 16, 00));
        flight2.setPrice(40F);

        List<FlightDTO> flights = Arrays.asList(flight1, flight2);

        // Call the method under test
        List<FlightRouteDTO> availableRoutes = flightsRouteService.getAvailableRoutes(queryDTO, flights);

        // Assertions
        assertNotNull(availableRoutes);
        assertEquals(2, availableRoutes.size());
        FlightRouteDTO route = availableRoutes.get(0);
        assertEquals(10F, route.getPrice());
        assertEquals(LocalDateTime.of(2024, 4, 1, 13, 00), route.getDepartureDate());
        assertEquals(LocalDateTime.of(2024, 4, 1, 14, 00), route.getLandingDate());
        assertEquals(1, route.getDuration());
    }
}