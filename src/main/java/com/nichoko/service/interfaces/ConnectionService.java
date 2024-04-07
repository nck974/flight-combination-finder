package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.query.ConnectionQueryDTO;
import com.nichoko.domain.dto.query.RouteQueryDTO;

public interface ConnectionService {
    public ConnectionDTO saveConnection(ConnectionDTO connectionDTO);

    public List<ConnectionDTO> saveConnections(List<ConnectionDTO> connections);

    public List<ConnectionDTO> listSavedConnections();

    public List<ConnectionDTO> getConnectionsForAirport(ConnectionQueryDTO query);

    public List<List<ConnectionDTO>> getRoutesBetweenTwoAirports(RouteQueryDTO query);
}
