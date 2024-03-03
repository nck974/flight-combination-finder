package com.nichoko;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Path("/test")
    @POST
    public RestResponse<List<FlightDTO>> getAllFlightsTest(FlightQueryDTO query) {
        List<FlightDTO> flights = new ArrayList<>();

        FlightDTO flight = new FlightDTO();
        flight.setOrigin("STD");
        flight.setDestination("NUE");
        flight.setPrice(19.99f);
        flight.setDepartureDate(LocalDateTime.of(2024, 3, 5, 12, 47, 0));
        flight.setLandingDate(LocalDateTime.of(2024, 3, 5, 15, 47, 0));
        flight.setId(1l);
        flight.setCreatedAt(LocalDateTime.now());
        flights.add(flight);

        FlightDTO flight2 = new FlightDTO();
        flight2.setOrigin("STD");
        flight2.setDestination("NUE");
        flight2.setPrice(20.99f);
        flight2.setDepartureDate(LocalDateTime.of(2024, 3, 8, 23, 47, 0));
        flight2.setLandingDate(LocalDateTime.of(2024, 3, 9, 2, 47, 0));
        flight2.setId(1l);
        flight2.setCreatedAt(LocalDateTime.now());
        flights.add(flight2);

        return RestResponse.ok(flights);
    }
}
