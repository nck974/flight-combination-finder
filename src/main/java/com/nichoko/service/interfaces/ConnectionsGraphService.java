package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.graph.ConnectionsGraphDTO;

public interface ConnectionsGraphService {
    public ConnectionsGraphDTO getRoutesGraph(List<List<ConnectionDTO>> routes);
}
