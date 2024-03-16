package com.nichoko.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import com.nichoko.domain.dao.Connection;
import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.mapper.ConnectionMapper;
import com.nichoko.repository.ConnectionRepository;
import com.nichoko.service.interfaces.ConnectionService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConnectionServiceImpl implements ConnectionService {

    private Logger logger = Logger.getLogger(ConnectionServiceImpl.class);

    ConnectionMapper mapper;
    private ConnectionRepository connectionRepository;

    @Inject
    ConnectionServiceImpl(ConnectionMapper mapper, ConnectionRepository connectionRepository) {
        this.mapper = mapper;
        this.connectionRepository = connectionRepository;
    }

    @Transactional
    public ConnectionDTO saveConnection(ConnectionDTO connectionDTO) {
        logger.info("Saving into the database " + connectionDTO.getOrigin() + " - " + connectionDTO.getDestination()
                + "...");
        Connection connection = mapper.toDAO(connectionDTO);
        logger.info("Persisting into the database " + connection.origin + " - " + connection.destination + "...");
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
}
