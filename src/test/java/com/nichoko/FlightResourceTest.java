package com.nichoko;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import com.nichoko.domain.dto.FlightDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.time.LocalDateTime;

@QuarkusTest
class FlightResourceTest {
    @Test
    void testCreateFlightEndpoint() {
        FlightDTO flight = new FlightDTO();
        flight.setOrigin("STD");
        flight.setDestination("SAN");
        flight.setPrice(500.0f);
        flight.setDepartureDate(LocalDateTime.now());
        flight.setLandingDate(LocalDateTime.now());

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(flight)
                .when().post("/flights")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("origin", is(flight.getOrigin()))
                .body("destination", is(flight.getDestination()))
                .body("price", is(flight.getPrice()))
                .body("departureDate", notNullValue())
                .body("landingDate", notNullValue())
                .body("createdAt", notNullValue());
    }    
    
    @Test
    void testGetFlightsEndpoint() {
        given()
                .when().get("/flights")
                .then()
                .log().all()
                .statusCode(200);
    }

}