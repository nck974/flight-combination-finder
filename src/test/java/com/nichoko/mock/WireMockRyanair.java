package com.nichoko.mock;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockRyanair implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();

        wireMockServer.stubFor(get(urlMatching("/farfnd/v4/roundTripFares.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RyanairMock.getRyanairGetFlightsMock())));

        wireMockServer.stubFor(get(urlMatching("/views/locate/searchWidget/routes/en/airport/.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RyanairMock.getRyanairGetAirportConnectionsMock())));

        wireMockServer.stubFor(get(urlMatching("/views/locate/searchWidget/routes/en/airport/WRN"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"something\"}")));

        // Everything not stub is redirected to a local URL to avoid calling real
        // backends on test
        wireMockServer.stubFor(get(urlMatching(".*")).atPriority(10)
                .willReturn(aResponse().proxiedFrom("https://localhost/backend")));

        // This property must match the one of the application.properties of the real
        // server
        return Map.of("quarkus.rest-client.ryanair.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}