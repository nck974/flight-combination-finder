package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.ConnectionDTO;

public interface ConnectionService {
    public ConnectionDTO saveConnection(ConnectionDTO connectionDTO);

    public List<ConnectionDTO> saveConnections(List<ConnectionDTO> connections);

    public List<ConnectionDTO> listSavedConnections();
}
