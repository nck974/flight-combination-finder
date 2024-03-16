package com.nichoko.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.service.FlightDetailsServiceImpl;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class FlightDetailsServiceImplTest {

    @Test
    void testSetFlightsDuration() {
        // Create some sample flights
        List<FlightDTO> flights = new ArrayList<>();
        FlightDTO flight1 = new FlightDTO();
        flight1.setDepartureDate(LocalDateTime.of(2024, 3, 13, 10, 0));
        flight1.setLandingDate(LocalDateTime.of(2024, 3, 13, 12, 30));
        flights.add(flight1);

        FlightDTO flight2 = new FlightDTO();
        flight2.setDepartureDate(LocalDateTime.of(2024, 3, 13, 15, 0));
        flight2.setLandingDate(LocalDateTime.of(2024, 3, 14, 1, 30));
        flights.add(flight2);

        FlightDTO flight3 = new FlightDTO();
        flight3.setDepartureDate(LocalDateTime.of(2024, 3, 13, 15, 0));
        flight3.setLandingDate(LocalDateTime.of(2024, 3, 13, 15, 30));
        flights.add(flight3);

        // Call the method
        FlightDetailsServiceImpl flightService = new FlightDetailsServiceImpl();
        List<FlightDTO> resultFlights = flightService.setFlightsDuration(flights);

        // Assert the result
        assertEquals(3, resultFlights.size());

        // Assert duration calculation for flight1
        assertEquals(3, resultFlights.get(0).getDuration());

        // Assert duration calculation for flight2
        assertEquals(11, resultFlights.get(1).getDuration());

        // Assert duration calculation for flight3
        assertEquals(1, resultFlights.get(2).getDuration());
    }
}
