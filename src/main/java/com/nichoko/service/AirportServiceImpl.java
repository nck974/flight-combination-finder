package com.nichoko.service;

import com.nichoko.domain.dao.Airport;
import com.nichoko.domain.dto.AirportDTO;
import com.nichoko.domain.mapper.AirportMapper;
import com.nichoko.exception.AirportNotFoundException;
import com.nichoko.repository.AirportRepository;
import com.nichoko.service.interfaces.AirportService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AirportServiceImpl implements AirportService {
    private AirportRepository airportRepository;
    private AirportMapper airportMapper;

    @Inject
    AirportServiceImpl(AirportRepository airportRepository, AirportMapper airportMapper) {
        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
    }

    @Override
    public AirportDTO getAirport(String iataCode) {
        Airport airport = airportRepository.find("iataCode", iataCode).firstResult();

        if (airport == null) {
            throw new AirportNotFoundException(iataCode);
        }

        return airportMapper.toDTO(airport);
    }

}
