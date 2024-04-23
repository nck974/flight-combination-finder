package com.nichoko.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightRouteDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO.RouteCombination;
import com.nichoko.exception.ErrorFetchingDataException;
import com.nichoko.mock.FlightMock;
import com.nichoko.service.RyanairService;
import com.nichoko.service.VuelingService;
import com.nichoko.service.interfaces.FlightService;
import com.nichoko.service.interfaces.FlightsRouteService;

@QuarkusTest
class FlightResourceTest {

    @InjectMock
    private RyanairService ryanairService;

    @InjectMock
    private VuelingService vuelingService;

    @InjectMock
    private FlightService flightService;

    @InjectMock
    private FlightsRouteService flightsRouteService;

    private FlightQueryDTO getMockQuery() {
        FlightQueryDTO queryDTO = new FlightQueryDTO();

        RouteCombination route = new RouteCombination("SDR", "STD");
        queryDTO.setRoutes(List.of(route));
        queryDTO.setStartDate(LocalDate.of(2024, 3, 10));
        queryDTO.setEndDate(LocalDate.of(2024, 4, 30));
        return queryDTO;
    }

    @Test
    void testGetAllFlights__success() {
        FlightQueryDTO queryDTO = this.getMockQuery();

        // Set up mock responses
        List<FlightDTO> mockFlights = Arrays.asList(FlightMock.getFlightMock());
        List<FlightRouteDTO> mockRoutes = Arrays.asList(new FlightRouteDTO(), new FlightRouteDTO());

        // Mock company queries. New companies have to be added here
        when(ryanairService.getCompanyFlights(any(FlightQueryDTO.class))).thenReturn(mockFlights);
        when(vuelingService.getCompanyFlights(any(FlightQueryDTO.class))).thenReturn(new ArrayList<>());

        // Mock db and routes
        when(flightService.saveFlights(anyList())).thenReturn(mockFlights);
        when(flightsRouteService.getAvailableRoutes(any(FlightQueryDTO.class), anyList())).thenReturn(mockRoutes);

        given()
                .contentType(ContentType.JSON)
                .body(queryDTO)
                .when().post("/backend/flights")
                .then()
                .statusCode(200)
                .body("flights.size()", is(mockFlights.size()))
                .body("availableRoutes.size()", is(mockRoutes.size()));

        // Verify method invocations
        verify(ryanairService, times(1)).getCompanyFlights(any(FlightQueryDTO.class));
        verify(vuelingService, times(1)).getCompanyFlights(any(FlightQueryDTO.class));
        verify(flightService, times(1)).saveFlights(anyList());
        verify(flightsRouteService, times(1)).getAvailableRoutes(any(FlightQueryDTO.class), anyList());
    }

    @Test
    void testGetAllFlights__noFlights() {
        FlightQueryDTO queryDTO = this.getMockQuery();

        // Set up mock responses
        List<FlightDTO> mockFlights = new ArrayList<>();

        // Mock company queries. New companies have to be added here
        when(ryanairService.getCompanyFlights(any(FlightQueryDTO.class))).thenReturn(mockFlights);
        when(vuelingService.getCompanyFlights(any(FlightQueryDTO.class))).thenReturn(new ArrayList<>());

        // Mock db ids
        when(flightService.saveFlights(anyList())).thenReturn(mockFlights);

        given()
                .contentType(ContentType.JSON)
                .body(queryDTO)
                .when().post("/backend/flights")
                .then()
                .statusCode(404)
                .body("message", is(instanceOf(String.class)))
                .body("code", is(instanceOf(Integer.class)));

    }

    @Test
    void testGetAllFlights__errorFetchingData() {
        FlightQueryDTO queryDTO = this.getMockQuery();

        when(ryanairService.getCompanyFlights(any(FlightQueryDTO.class)))
                .thenThrow(new ErrorFetchingDataException(new RuntimeException()));
        when(vuelingService.getCompanyFlights(any(FlightQueryDTO.class)))
                .thenThrow(new ErrorFetchingDataException(new RuntimeException()));

        given()
                .contentType(ContentType.JSON)
                .body(queryDTO)
                .when().post("/backend/flights")
                .then()
                .statusCode(500)
                .body("message", is(instanceOf(String.class)))
                .body("code", is(instanceOf(Integer.class)));

    }

}