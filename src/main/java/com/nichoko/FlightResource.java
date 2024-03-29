package com.nichoko;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;
import com.nichoko.domain.dto.response.ItineraryResponseDTO;
import com.nichoko.exception.NoFlightsFoundException;
import com.nichoko.service.interfaces.AirlineService;
import com.nichoko.service.interfaces.FlightService;
import com.nichoko.service.interfaces.FlightsDetailsService;

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
    private FlightsDetailsService flightsDetailsService;

    @Inject
    FlightResource(AirlineService airlineService, FlightService flightService,
            FlightsDetailsService flightsDetailsService) {
        this.airlineService = airlineService;
        this.flightService = flightService;
        this.flightsDetailsService = flightsDetailsService;
    }

    @POST
    public RestResponse<ItineraryResponseDTO> getAllFlights(FlightQueryDTO query) {
        logger.info("Checking flights:\n" + query.getRoutesCombinations() + "...");
        List<FlightDTO> flights = airlineService.getCompanyFlights(query);
        logger.info("Flights found: " + flights.size());

        if (!flights.isEmpty()) {
            logger.info("Saving to the database:\n" + query.getRoutesCombinations() + "...");
            flights = flightService.saveFlights(flights);
            flights = this.flightsDetailsService.setFlightsDuration(flights);
        }else{
            throw new NoFlightsFoundException();
        }
        ItineraryResponseDTO itineraryResponseDTO = new ItineraryResponseDTO();
        itineraryResponseDTO.setFlights(flights);
        itineraryResponseDTO.setAvailableRoutes(this.flightsDetailsService.getItineraryOptions(query, flights));

        return RestResponse.ok(itineraryResponseDTO);
    }

    @Path("/test")
    @POST
    public RestResponse<ItineraryResponseDTO> getAllFlightsTest(FlightQueryDTO query) {
        logger.info("Generating test response...");
        List<FlightDTO> flights = new ArrayList<>();

        FlightDTO flight = new FlightDTO();
        flight.setOrigin("NUE");
        flight.setDestination("STN");
        flight.setPrice(19.99f);
        flight.setDepartureDate(LocalDateTime.of(2024, 3, 25, 12, 47, 0));
        flight.setLandingDate(LocalDateTime.of(2024, 3, 25, 15, 47, 0));
        flight.setId(1l);
        flight.setCreatedAt(LocalDateTime.now());
        flights.add(flight);

        FlightDTO flight2 = new FlightDTO();
        flight2.setOrigin("NUE");
        flight2.setDestination("STN");
        flight2.setPrice(20.99f);
        flight2.setDepartureDate(LocalDateTime.of(2024, 3, 16, 23, 47, 0));
        flight2.setLandingDate(LocalDateTime.of(2024, 3, 17, 2, 47, 0));
        flight2.setId(1l);
        flight2.setCreatedAt(LocalDateTime.now());
        flights.add(flight2);

        FlightDTO flight3 = new FlightDTO();
        flight3.setOrigin("STN");
        flight3.setDestination("SDR");
        flight3.setPrice(20.99f);
        flight3.setDepartureDate(LocalDateTime.of(2024, 3, 25, 23, 47, 0));
        flight3.setLandingDate(LocalDateTime.of(2024, 3, 26, 2, 47, 0));
        flight3.setId(1l);
        flight3.setCreatedAt(LocalDateTime.now());
        flights.add(flight3);

        flights = this.flightsDetailsService.setFlightsDuration(flights);

        ItineraryResponseDTO itineraryResponseDTO = new ItineraryResponseDTO();
        itineraryResponseDTO.setFlights(flights);
        itineraryResponseDTO.setAvailableRoutes(this.flightsDetailsService.getItineraryOptions(query, flights));

        return RestResponse.ok(itineraryResponseDTO);
    }

}
