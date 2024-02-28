package com.nichoko;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightQueryDTO;
import com.nichoko.service.interfaces.AirlineService;
import com.nichoko.service.interfaces.FlightService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FlightResource {

    private Logger logger = Logger.getLogger(FlightResource.class);

    private AirlineService airlineService;
    private FlightService flightService;

    @Inject
    FlightResource(AirlineService airlineService, FlightService flightService) {
        this.airlineService = airlineService;
        this.flightService = flightService;
    }

    @POST
    public RestResponse<List<FlightDTO>> getAllFlights(FlightQueryDTO query) {
        logger.info("Checking flights " + query.getOrigin() + " to " + query.getDestination() + "...");
        List<FlightDTO> flights = airlineService.getCompanyFlights(query);
        logger.info("Flights found: " + flights.size());

        if (!flights.isEmpty()) {
            logger.info("Saving to the database " + query.getOrigin() + " to " + query.getDestination() + "...");
            flights = flightService.saveFlights(flights);
        }

        return RestResponse.ok(flights);
    }
}
