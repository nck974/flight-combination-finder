package com.nichoko.mock;

import java.time.LocalDateTime;

import com.nichoko.domain.dto.FlightDTO;

public class FlightMock {
    FlightMock() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    public static FlightDTO getFlightMock() {
        FlightDTO flight = new FlightDTO();
        flight.setOrigin("SDR");
        flight.setDestination("OVD");
        flight.setDepartureDate(LocalDateTime.of(2024, 4, 1, 12, 00));
        flight.setLandingDate(LocalDateTime.of(2024, 4, 1, 10, 00));
        flight.setPrice(10F);
        return flight;
    }
}
