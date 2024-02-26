package com.nichoko.service;

import java.util.List;
import java.util.stream.Collectors;

import com.nichoko.domain.dao.Flight;
import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.mapper.FlightMapper;
import com.nichoko.repository.FlightRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FlightService {
    FlightMapper mapper;
    private FlightRepository flightRepository;

    @Inject
    FlightService(FlightMapper mapper, FlightRepository flightRepository) {
        this.mapper = mapper;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public FlightDTO saveFlight(FlightDTO flightDTO) {
        Flight flight = mapper.toDAO(flightDTO);
        flightRepository.persistAndFlush(flight);
        return mapper.toDTO(flight);
    }

    public List<FlightDTO> listFlights() {
        return flightRepository.listAll()
                .stream()
                .map(flight -> mapper.toDTO(flight))
                .collect(Collectors.toList());
    }
}
