package com.nichoko;

import java.util.List;
import org.jboss.resteasy.reactive.RestResponse;

import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.graph.ConnectionsGraphDTO;
import com.nichoko.domain.dto.query.ConnectionQueryDTO;
import com.nichoko.domain.dto.query.RouteQueryDTO;
import com.nichoko.domain.dto.response.ConnectionResponseDTO;
import com.nichoko.domain.dto.response.RouteResponseDTO;
import com.nichoko.service.interfaces.ConnectionService;
import com.nichoko.service.interfaces.ConnectionsGraphService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.jbosslog.JBossLog;

@Path("/airports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@JBossLog
public class AirportResource {

    private ConnectionService connectionService;
    private ConnectionsGraphService connectionsGraphService;

    @Inject
    AirportResource(ConnectionService connectionService, ConnectionsGraphService connectionsGraphService) {
        this.connectionService = connectionService;
        this.connectionsGraphService = connectionsGraphService;
    }

    /**
     * Shared method to log the queries sent
     * 
     * @param query
     * @return
     */
    private List<List<ConnectionDTO>> getRoutesBetweenTwoAirports(RouteQueryDTO query) {
        log.info("Searching connections connections between: " + query.getOrigin() + " and " + query.getDestination()
                + "...");
        List<List<ConnectionDTO>> routes = connectionService.getRoutesBetweenTwoAirports(query);
        log.info("Number of connections found: " + routes.size());
        return routes;
    }

    /**
     * Return all the airports that connect the the provided airport in the query
     * 
     * @param query
     * @return
     */
    @POST
    @Path("/connections")
    public RestResponse<ConnectionResponseDTO> getAllConnectionsForAirport(ConnectionQueryDTO query) {
        log.info("Searching airport connections for airport with code: " + query.getOrigin() + "...");
        List<ConnectionDTO> connections = connectionService.getConnectionsForAirport(query);
        log.info("Number of connections found: " + connections.size());

        ConnectionResponseDTO connectionsResponseDTO = new ConnectionResponseDTO();
        connectionsResponseDTO.setConnections(connections);

        return RestResponse.ok(connectionsResponseDTO);
    }

    /**
     * Return the available routes that connect two airports through direct flights
     * 
     * @param query
     * @return
     */
    @POST
    @Path("/routes")
    public RestResponse<RouteResponseDTO> getAllConnectionsBetweenTwoAirports(RouteQueryDTO query) {

        List<List<ConnectionDTO>> routes = getRoutesBetweenTwoAirports(query);

        RouteResponseDTO routeResponseDTO = new RouteResponseDTO(routes);

        return RestResponse.ok(routeResponseDTO);
    }

    /**
     * Return the data formatted to be represented in a graph of the connections
     * between two airports
     * 
     * @param query
     * @return
     */
    @POST
    @Path("/routes/graph")
    public RestResponse<ConnectionsGraphDTO> getRoutesGraphBetweenAirports(RouteQueryDTO query) {

        List<List<ConnectionDTO>> routes = getRoutesBetweenTwoAirports(query);

        ConnectionsGraphDTO graph = connectionsGraphService.getRoutesGraph(routes);

        return RestResponse.ok(graph);
    }

}
