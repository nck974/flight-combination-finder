package com.nichoko.service;

import java.util.List;
import java.util.stream.Collectors;

import com.nichoko.domain.dao.Flight;
import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.mapper.FlightMapper;
import com.nichoko.repository.FlightRepository;
import com.nichoko.service.interfaces.FlightService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@JBossLog
public class FlightServiceImpl implements FlightService {

    FlightMapper mapper;
    private FlightRepository flightRepository;

    @Inject
    FlightServiceImpl(FlightMapper mapper, FlightRepository flightRepository) {
        this.mapper = mapper;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public FlightDTO saveFlight(FlightDTO flightDTO) {
        log.debug("Saving into the database " + flightDTO.getOrigin() + " - " + flightDTO.getDestination() + "...");
        Flight flight = mapper.toDAO(flightDTO);
        log.debug("Persisting into the database " + flight.origin + " - " + flight.destination + "...");
        flightRepository.persistAndFlush(flight);
        return mapper.toDTO(flight);
    }

    @Transactional
    public List<FlightDTO> saveFlights(List<FlightDTO> flights) {
        return flights.stream().map(this::saveFlight).collect(Collectors.toList());
    }

    public List<FlightDTO> listSavedFlights() {
        return flightRepository.listAll()
                .stream()
                .map(flight -> mapper.toDTO(flight))
                .collect(Collectors.toList());
    }
}
