package com.nichoko.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.graph.ConnectionsGraphCategoryDTO;
import com.nichoko.domain.dto.graph.ConnectionsGraphDTO;
import com.nichoko.domain.dto.graph.ConnectionsGraphLinkDTO;
import com.nichoko.domain.dto.graph.ConnectionsGraphNodeDTO;
import com.nichoko.service.interfaces.ConnectionsGraphService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConnectionsGraphServiceImpl implements ConnectionsGraphService {

    @Override
    public ConnectionsGraphDTO getRoutesGraph(List<List<ConnectionDTO>> routes) {

        ConnectionsGraphDTO graph = new ConnectionsGraphDTO();

        List<ConnectionsGraphNodeDTO> nodes = new ArrayList<>();
        List<ConnectionsGraphCategoryDTO> categories = new ArrayList<>();
        List<ConnectionsGraphLinkDTO> links = new ArrayList<>();

        // Set an ID for each airport
        Long idCounter = 0l;
        Map<String, Long> airportIdMapping = new HashMap<>();

        // Get all airports unique
        for (List<ConnectionDTO> route : routes) {

            StringBuilder routeNameBuilder = new StringBuilder();
            List<ConnectionsGraphLinkDTO> routeLinks = new ArrayList<>();
            for (ConnectionDTO connection : route) {
                List<String> airports = List.of(connection.getOrigin(), connection.getDestination());

                // Build graph nodes
                idCounter = buildAirportNodes(nodes, categories, idCounter, airportIdMapping, airports);

                // Build graph name
                routeNameBuilder.append(connection.getOrigin());
                routeNameBuilder.append("->");
                if (connection == route.get(route.size() - 1)) {
                    routeNameBuilder.append(connection.getDestination());
                }

                // Build link
                ConnectionsGraphLinkDTO link = new ConnectionsGraphLinkDTO();
                link.setSource(airportIdMapping.get(connection.getOrigin()));
                link.setTarget(airportIdMapping.get(connection.getDestination()));
                routeLinks.add(link);

            }

            String routeName = routeNameBuilder.toString();
            for (ConnectionsGraphLinkDTO link : routeLinks) {
                link.setRouteName(routeName);
            }

            links.addAll(routeLinks);
        }

        graph.setNodes(nodes);
        graph.setCategories(categories);
        graph.setLinks(links);

        return graph;
    }

    private Long buildAirportNodes(List<ConnectionsGraphNodeDTO> nodes,
            List<ConnectionsGraphCategoryDTO> categories,
            Long idCounter,
            Map<String, Long> airportIdMapping,
            List<String> airports) {
        for (String airport : airports) {
            if (airportIdMapping.containsKey(airport)) {
                continue;
            }
            airportIdMapping.put(airport, idCounter);

            // Graph node
            ConnectionsGraphNodeDTO node = buildNode(idCounter, airport);

            nodes.add(node);
            categories.add(new ConnectionsGraphCategoryDTO(airport));

            idCounter++;
        }
        return idCounter;
    }

    private ConnectionsGraphNodeDTO buildNode(Long idCounter, String originAirport) {
        ConnectionsGraphNodeDTO node = new ConnectionsGraphNodeDTO();
        node.setId(idCounter);
        node.setName(originAirport);
        node.setCategory(idCounter);
        node.setSymbolSize(30.0f);
        node.setValue(50.0f);
        return node;
    }

}
