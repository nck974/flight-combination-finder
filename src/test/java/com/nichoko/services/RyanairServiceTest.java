package com.nichoko.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.query.ConnectionQueryDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO.RouteCombination;
import com.nichoko.exception.ErrorFetchingDataException;
import com.nichoko.service.interfaces.AirlineService;
import com.nichoko.mock.WireMockRyanair;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(WireMockRyanair.class)
class RyanairServiceTest {

    @Inject
    private AirlineService airlineService;

    private FlightQueryDTO getMockQueryTwoConnections() {
        FlightQueryDTO queryDTO = new FlightQueryDTO();

        RouteCombination route1 = new RouteCombination("SDR", "OVD");
        RouteCombination route2 = new RouteCombination("OVD", "STN");
        queryDTO.setRoutes(List.of(route1, route2));
        queryDTO.setStartDate(LocalDate.of(2024, 3, 10));
        queryDTO.setEndDate(LocalDate.of(2024, 4, 30));
        return queryDTO;
    }

    private FlightQueryDTO getMockQueryOneConnection() {
        FlightQueryDTO queryDTO = new FlightQueryDTO();

        RouteCombination route1 = new RouteCombination("SDR", "OVD");
        queryDTO.setRoutes(List.of(route1));
        queryDTO.setStartDate(LocalDate.of(2024, 3, 10));
        queryDTO.setEndDate(LocalDate.of(2024, 4, 30));
        return queryDTO;
    }

    @Test
    void testGetCompanyFlights_withMultipleUrls() throws Exception {
        FlightQueryDTO query = this.getMockQueryTwoConnections();
        List<FlightDTO> flights = airlineService.getCompanyFlights(query);

        assertEquals(4, flights.size());
    }

    @Test
    void testGetCompanyFlights_withOneUrl() throws Exception {
        FlightQueryDTO query = this.getMockQueryOneConnection();
        List<FlightDTO> flights = airlineService.getCompanyFlights(query);

        assertEquals(2, flights.size());
    }

    @Test
    void testGetCompanyAirportConnections__success() throws Exception {
        ConnectionQueryDTO query = new ConnectionQueryDTO();
        query.setOrigin("STD");
        List<ConnectionDTO> connections = airlineService.getAirportConnections(query);

        assertEquals(2, connections.size());
    }

    @Test
    void testGetCompanyAirportConnections__error() throws Exception {
        ConnectionQueryDTO query = new ConnectionQueryDTO();
        query.setOrigin("WRN");
        assertThrows(ErrorFetchingDataException.class, () -> {
            airlineService.getAirportConnections(query);
        });

    }
}
