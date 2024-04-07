package com.nichoko.service.query;

import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@RegisterRestClient(configKey = "ryanair")
public interface RyanairQueryService {

    @GET
    @Path("/farfnd/v4/roundTripFares")
    @Produces(MediaType.APPLICATION_JSON)
    Response getFlightsForDate(@RestQuery Map<String, String> parameters);

    @GET
    @Path("/views/locate/searchWidget/routes/en/airport/{iataCode}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getAirportConnections(@PathParam("iataCode") String iataCode);
}
