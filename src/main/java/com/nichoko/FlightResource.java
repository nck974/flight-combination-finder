package com.nichoko;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.reactive.RestResponse;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightRouteDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;
import com.nichoko.domain.dto.response.ItineraryResponseDTO;
import com.nichoko.exception.NoFlightsFoundException;
import com.nichoko.service.interfaces.AirlineService;
import com.nichoko.service.interfaces.FlightService;
import com.nichoko.service.interfaces.FlightsRouteService;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.jbosslog.JBossLog;

@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@JBossLog
public class FlightResource {

    private Instance<AirlineService> airlineServices;
    private FlightService flightService;
    private FlightsRouteService flightsRouteService;

    @Inject
    FlightResource(
            Instance<AirlineService> airlineServices,
            FlightService flightService,
            FlightsRouteService flightsRouteService) {
        this.airlineServices = airlineServices;
        this.flightService = flightService;
        this.flightsRouteService = flightsRouteService;
    }

    /**
     * Main endpoints to obtain the connections of the airports
     * 
     * @param query
     * @return
     */
    @POST
    public RestResponse<ItineraryResponseDTO> getAllFlights(FlightQueryDTO query) {
        List<FlightDTO> flights = new ArrayList<>();
        for (AirlineService airlineService : this.airlineServices) {
            log.info("Checking " + airlineService.getAirlineName() + " flights for route:\n"
                    + query.getRoutesCombinations());
            List<FlightDTO> airlineFlights = airlineService.getCompanyFlights(query);
            log.info("Number of flights found in the route: " + airlineFlights.size());

            if (airlineFlights.isEmpty()) {
                continue;
            }
            log.info("Saving flights in the local database...");
            airlineFlights = flightService.saveFlights(airlineFlights);
            flights.addAll(airlineFlights);
        }

        if (flights.isEmpty()) {
            throw new NoFlightsFoundException();
        }

        log.info("Calculating available routes...");
        List<FlightRouteDTO> availableRoutes = this.flightsRouteService.getAvailableRoutes(query, flights);
        log.info("Number of available routes found: " + availableRoutes.size());

        ItineraryResponseDTO itineraryResponseDTO = new ItineraryResponseDTO();
        itineraryResponseDTO.setFlights(flights);
        itineraryResponseDTO.setAvailableRoutes(availableRoutes);

        return RestResponse.ok(itineraryResponseDTO);
    }

    /**
     * This method is only for test purposes to develop in frontend or te verify the
     * correct
     * communication of the service. It shall be removed in the future
     * 
     * @param query
     * @return
     */
    @Path("/test")
    @POST
    public RestResponse<ItineraryResponseDTO> getAllFlightsTest(FlightQueryDTO query) {
        log.info("Generating test response...");
        List<FlightDTO> flights = new ArrayList<>();

        FlightDTO flight = new FlightDTO();
        flight.setOrigin("NUE");
        flight.setDestination("STN");
        flight.setPrice(19.99f);
        flight.setDepartureDate(LocalDateTime.of(2024, 3, 25, 12, 47, 0));
        flight.setLandingDate(LocalDateTime.of(2024, 3, 25, 15, 47, 0));
        flight.setDuration(4);
        flight.setId(1l);
        flight.setCreatedAt(LocalDateTime.now());
        flights.add(flight);

        FlightDTO flight2 = new FlightDTO();
        flight2.setOrigin("NUE");
        flight2.setDestination("STN");
        flight2.setPrice(20.99f);
        flight2.setDepartureDate(LocalDateTime.of(2024, 3, 16, 23, 47, 0));
        flight2.setLandingDate(LocalDateTime.of(2024, 3, 17, 2, 47, 0));
        flight.setDuration(4);
        flight2.setId(1l);
        flight2.setCreatedAt(LocalDateTime.now());
        flights.add(flight2);

        FlightDTO flight3 = new FlightDTO();
        flight3.setOrigin("STN");
        flight3.setDestination("SDR");
        flight3.setPrice(20.99f);
        flight3.setDepartureDate(LocalDateTime.of(2024, 3, 25, 23, 47, 0));
        flight3.setLandingDate(LocalDateTime.of(2024, 3, 26, 2, 47, 0));
        flight.setDuration(4);
        flight3.setId(1l);
        flight3.setCreatedAt(LocalDateTime.now());
        flights.add(flight3);

        ItineraryResponseDTO itineraryResponseDTO = new ItineraryResponseDTO();
        itineraryResponseDTO.setFlights(flights);
        itineraryResponseDTO.setAvailableRoutes(this.flightsRouteService.getAvailableRoutes(query, flights));

        return RestResponse.ok(itineraryResponseDTO);
    }

}
