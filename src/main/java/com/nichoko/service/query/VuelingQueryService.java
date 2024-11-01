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
@RegisterRestClient(configKey = "vueling")
public interface VuelingQueryService {

    @GET
    @Path("/FlightPrice/GetAllFlights")
    @ClientHeaderParam(name = "User-Agent", value = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:131.0) Gecko/20100101 Firefox/131.0")
    @ClientHeaderParam(name = "Accept", value = "application/json")
    @ClientHeaderParam(name = "Accept-Language", value = "en-US,en;q=0.5")
    @ClientHeaderParam(name = "Accept-Encoding", value = "gzip")
    @ClientHeaderParam(name = "DNT", value = "1")
    @ClientHeaderParam(name = "Sec-GPC", value = "1")
    @ClientHeaderParam(name = "Connection", value = "keep-alive")
    @ClientHeaderParam(name = "Upgrade-Insecure-Requests", value = "1")
    @ClientHeaderParam(name = "Sec-Fetch-Dest", value = "empty")
    @ClientHeaderParam(name = "Sec-Fetch-Mode", value = "no-cors")
    @ClientHeaderParam(name = "Sec-Fetch-Site", value = "same-origin")
    @ClientHeaderParam(name = "Sec-Fetch-User", value = "?1")
    @ClientHeaderParam(name = "TE", value = "trailers")
    @ClientHeaderParam(name = "Priority", value = "u=4")
    @ClientHeaderParam(name = "Pragma", value = "no-cache")
    @ClientHeaderParam(name = "Cache-Control", value = "no-cache")
    @ClientHeaderParam(name = "Referer", value = "https://www.vueling.com/de")
    @Consumes(MediaType.APPLICATION_JSON)
    Response getFlightsForDate(@RestQuery Map<String, String> parameters);
    
    @GET
    @ClientHeaderParam(name = "User-Agent", value = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:131.0) Gecko/20100101 Firefox/131.0")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/FlightTimetables/GetFlightTimeForeachMonthInRange")
    Response getFlightsLandingDate(@RestQuery Map<String, String> parameters);

    @GET
    @ClientHeaderParam(name = "User-Agent", value = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:131.0) Gecko/20100101 Firefox/131.0")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/Markets/GetAllMarketsSearcher")
    Response getAllAirportConnections();
}
