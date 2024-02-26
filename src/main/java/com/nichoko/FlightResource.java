package com.nichoko;

import java.util.List;

import org.jboss.resteasy.reactive.RestResponse;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.service.FlightService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FlightResource {

    private FlightService flightService;

    @Inject
    FlightResource(FlightService flightService) {
        this.flightService = flightService;
    }

    @POST
    public RestResponse<FlightDTO> saveFlight(FlightDTO flight) {
        FlightDTO savedFlight = flightService.saveFlight(flight);
        return RestResponse.ok(savedFlight);
    }

    @GET
    public RestResponse<List<FlightDTO>> getAllFlight(FlightDTO flight) {
        List<FlightDTO> flights = flightService.listFlights();
        return RestResponse.ok(flights);
    }
}
