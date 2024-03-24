package com.nichoko.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import com.nichoko.domain.dao.Connection;
import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.ConnectionQueryDTO;
import com.nichoko.domain.dto.RouteQueryDTO;
import com.nichoko.domain.mapper.ConnectionMapper;
import com.nichoko.exception.NoConnectionsFoundException;
import com.nichoko.exception.TooManyConnectionsException;
import com.nichoko.repository.ConnectionRepository;
import com.nichoko.service.interfaces.AirlineService;
import com.nichoko.service.interfaces.ConnectionService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@JBossLog
public class ConnectionServiceImpl implements ConnectionService {

    private static final int MAX_NR_CONNECTIONS = 3;

    ConnectionMapper mapper;
    private ConnectionRepository connectionRepository;
    private AirlineService airlineService;

    @Inject
    ConnectionServiceImpl(
            ConnectionMapper mapper,
            ConnectionRepository connectionRepository,
            AirlineService airlineService) {
        this.mapper = mapper;
        this.connectionRepository = connectionRepository;
        this.airlineService = airlineService;
    }

    @Transactional
    public ConnectionDTO saveConnection(ConnectionDTO connectionDTO) {
        log.info("Saving into the database " + connectionDTO.getOrigin() + " - " + connectionDTO.getDestination()
                + "...");
        Connection connection = mapper.toDAO(connectionDTO);
        log.info("Persisting into the database " + connection.origin + " - " + connection.destination + "...");
        connectionRepository.persistAndFlush(connection);
        return mapper.toDTO(connection);
    }

    @Transactional
    public List<ConnectionDTO> saveConnections(List<ConnectionDTO> connections) {
        return connections.stream().map(this::saveConnection).collect(Collectors.toList());
    }

    public List<ConnectionDTO> listSavedConnections() {
        return connectionRepository.listAll()
                .stream()
                .map(connection -> mapper.toDTO(connection))
                .collect(Collectors.toList());
    }

    /**
     * Returns all the possible connections for a given airport and saves them in
     * the database
     * 
     * @param query
     * @return
     */
    public List<ConnectionDTO> getConnectionsForAirport(ConnectionQueryDTO query) {
        List<ConnectionDTO> connections = airlineService.getAirportConnections(query);
        if (!connections.isEmpty()) {
            log.info("Saving to the database connections of: " + query.getOrigin() + "...");
            return this.saveConnections(connections);
        }
        throw new NoConnectionsFoundException("No connections could be found.", 4000);
    }

    /**
     * Search all connections for a given airport. If they already exist in the
     * cached map do not
     * make the query
     * 
     * @param seenAirports
     * @param originAirport
     * @return
     */
    private List<ConnectionDTO> getConnectionsForAirport(Map<String, List<ConnectionDTO>> seenAirports,
            String originAirport) {
        List<ConnectionDTO> connectionsForLevel;
        if (seenAirports.containsKey(originAirport)) {
            connectionsForLevel = seenAirports.get(originAirport);
        } else {
            ConnectionQueryDTO connectionQuery = new ConnectionQueryDTO(originAirport);
            connectionsForLevel = airlineService.getAirportConnections(connectionQuery);
            if (!connectionsForLevel.isEmpty()) {
                log.debug(
                        "Saving to the database connections of: " + originAirport + "...");
                connectionsForLevel = this.saveConnections(connectionsForLevel);
            }
            seenAirports.put(originAirport, connectionsForLevel);
        }
        return connectionsForLevel;
    }

    /**
     * Check the number of connections to avoid an exponential grow of the queries
     * sent to the
     * airline API
     * 
     * @param query
     */
    private void validateRouteQuery(RouteQueryDTO query) {
        if (query.getMaxNrConnections() > MAX_NR_CONNECTIONS) {
            throw new TooManyConnectionsException("It is not possible to search more than 3 connections", 40002);
        }

        if (query.getMaxNrConnections() == 0) {
            query.setMaxNrConnections(MAX_NR_CONNECTIONS);
        }
    }

    /**
     * Return the airport from which the connections will be searched. If it is the
     * first one it
     * will be the one of the query, otherwise it will be the last destination of
     * the current route
     * 
     * @param query
     * @param currentLevel
     * @param currentRoute
     * @return
     */
    private String getOriginAirport(RouteQueryDTO query, int currentLevel, List<ConnectionDTO> currentRoute) {
        String originAirport;
        log.info("Current level is: " + currentLevel);
        if (currentLevel > 1) {
            ConnectionDTO currentConnection = currentRoute.get(currentRoute.size() - 1);
            originAirport = currentConnection.getDestination();
        } else {
            originAirport = query.getOrigin();
        }
        return originAirport;
    }

    /**
     * Find all possible routes between two airports up to a limited number of
     * stops.
     * 
     * @param query
     * @return
     */
    public List<List<ConnectionDTO>> getRoutesBetweenTwoAirports(RouteQueryDTO query) {
        log.info("Checking airport routes for: " + query.getOrigin() + " to " + query.getDestination());
        validateRouteQuery(query);

        // Cache queries here
        Map<String, List<ConnectionDTO>> seenAirports = new HashMap<>();

        // Initialize a queue with an empty list as the first connection does not have
        // any previous
        // connection to add to the route
        Queue<List<ConnectionDTO>> connectionQueue = new LinkedList<>();
        connectionQueue.add(new ArrayList<>());

        int currentLevel = 1;

        // End result
        List<List<ConnectionDTO>> routes = new ArrayList<>();
        while (!connectionQueue.isEmpty() && currentLevel <= query.getMaxNrConnections()) {
            int queueSize = connectionQueue.size();
            for (int i = 0; i < queueSize; i++) {
                List<ConnectionDTO> currentRoute = connectionQueue.poll();

                String originAirport = getOriginAirport(query, currentLevel, currentRoute);
                List<ConnectionDTO> connectionsForLevel = getConnectionsForAirport(seenAirports, originAirport);

                // If the route reaches the destination add it to the final result,
                // otherwise put it in the queue to find new connections
                for (ConnectionDTO connection : connectionsForLevel) {
                    List<ConnectionDTO> newRoute = new ArrayList<>(currentRoute);
                    newRoute.add(connection);
                    if (connection.getDestination().equals(query.getDestination())) {
                        routes.add(newRoute);
                    } else {
                        connectionQueue.offer(newRoute);
                    }
                }
            }
            currentLevel++;
        }

        if (routes.isEmpty()) {
            throw new NoConnectionsFoundException("No routes could be found.", 4004);
        }

        return routes;
    }

}
