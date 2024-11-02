package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.AirportDTO;

public interface AirportService {
    public AirportDTO getAirport(String iataCode);
    public List<AirportDTO> searchAirports(String nameOrCity);
}
