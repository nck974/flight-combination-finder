package com.nichoko.service.query;

import java.util.Map;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@ClientHeaderParam(name = "User-Agent", value = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "vueling")
public interface VuelingQueryService {

    @GET
    @Path("/FlightPrice/GetAllFlights")
    Response getFlightsForDate(@RestQuery Map<String, String> parameters);
    
    @GET
    @Path("/FlightTimetables/GetFlightTimeForeachMonthInRange")
    Response getFlightsLandingDate(@RestQuery Map<String, String> parameters);

    @GET
    @Path("/Markets/GetAllMarketsSearcher")
    Response getAllAirportConnections();
}
