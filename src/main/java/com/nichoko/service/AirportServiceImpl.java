package com.nichoko.service;

import java.util.List;

import com.nichoko.domain.dao.Airport;
import com.nichoko.domain.dto.AirportDTO;
import com.nichoko.domain.mapper.AirportMapper;
import com.nichoko.exception.AirportNotFoundException;
import com.nichoko.exception.NoResultsForSearchException;
import com.nichoko.repository.AirportRepository;
import com.nichoko.service.interfaces.AirportService;

import io.quarkus.panache.common.Parameters;
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

    @Override
    public List<AirportDTO> searchAirports(String query) {
        List<Airport> airports = airportRepository
                .find("(name ILIKE :query or city ILIKE :query or iataCode ILIKE :query or icaoCode ILIKE :query) and name is not null and name != 'N/A'",
                        Parameters.with("query", "%" + query + "%"))
                .list();

        if (airports == null || airports.isEmpty()) {
            throw new NoResultsForSearchException();
        }

        return airports.stream().map(airportMapper::toDTO).toList();
    }

}
