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

    @POST
    @Path("/connections")
    public RestResponse<ConnectionResponseDTO> getAllConnectionsForAirport(ConnectionQueryDTO query) {
        log.info("Checking airport connections for: " + query.getOrigin() + "...");
        List<ConnectionDTO> connections = connectionService.getConnectionsForAirport(query);
        log.info("Connections found: " + connections.size());

        ConnectionResponseDTO connectionsResponseDTO = new ConnectionResponseDTO();
        connectionsResponseDTO.setConnections(connections);

        return RestResponse.ok(connectionsResponseDTO);
    }

    @POST
    @Path("/routes")
    public RestResponse<RouteResponseDTO> getAllConnectionsBetweenTwoAirports(RouteQueryDTO query) {

        List<List<ConnectionDTO>> routes = connectionService.getRoutesBetweenTwoAirports(query);
        RouteResponseDTO routeResponseDTO = new RouteResponseDTO(routes);

        return RestResponse.ok(routeResponseDTO);
    }

    @POST
    @Path("/routes/graph")
    public RestResponse<ConnectionsGraphDTO> getRoutesGraphBetweenAirports(RouteQueryDTO query) {

        List<List<ConnectionDTO>> routes = connectionService.getRoutesBetweenTwoAirports(query);
        ConnectionsGraphDTO graph = connectionsGraphService.getRoutesGraph(routes);

        return RestResponse.ok(graph);
    }

}
