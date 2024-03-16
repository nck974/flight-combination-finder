package com.nichoko;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.ConnectionQueryDTO;
import com.nichoko.domain.dto.ConnectionsResponseDTO;
import com.nichoko.exception.NoFlightsFoundException;
import com.nichoko.service.interfaces.AirlineService;
import com.nichoko.service.interfaces.ConnectionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/airports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AirportResource {

    private Logger logger = Logger.getLogger(AirportResource.class);

    private AirlineService airlineService;
    private ConnectionService connectionService;

    @Inject
    AirportResource(AirlineService airlineService, ConnectionService connectionService) {
        this.airlineService = airlineService;
        this.connectionService = connectionService;
    }

    @POST
    public RestResponse<ConnectionsResponseDTO> getAllConnections(ConnectionQueryDTO query) {
        logger.info("Checking airport connections for:" + query.getOrigin() + "...");
        List<ConnectionDTO> connections = airlineService.getAirportConnections(query);
        logger.info("Connections found: " + connections.size());

        if (!connections.isEmpty()) {
            logger.info("Saving to the database connections of: " + query.getOrigin() + "...");
            connections = connectionService.saveConnections(connections);
        } else {
            throw new NoFlightsFoundException("No flights could be found.", 4000);
        }
        ConnectionsResponseDTO connectionsResponseDTO = new ConnectionsResponseDTO();
        connectionsResponseDTO.setConnections(connections);

        return RestResponse.ok(connectionsResponseDTO);
    }

}
