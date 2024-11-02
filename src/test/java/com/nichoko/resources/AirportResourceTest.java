package com.nichoko.resources;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AirportResourceTest {

    @Test
    void testSearchAirport__success() {

        given()
                .when().get("/backend/airports/search?airport=bilbao")
                .then()
                .statusCode(200);
    }

    @Test
    void testSearchAirport__invalidParameter() {

        given()
                .when().get("/backend/airports/search?invalid=bilbao")
                .then()
                .statusCode(400);
    }

    @Test
    void testSearchAirport__noResults() {

        given()
                .when().get("/backend/airports/search?airport=not-found-airport")
                .then()
                .statusCode(404);
    }
}
