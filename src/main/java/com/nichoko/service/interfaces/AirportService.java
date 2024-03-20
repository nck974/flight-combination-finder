package com.nichoko.service.interfaces;

import com.nichoko.domain.dto.AirportDTO;

public interface AirportService {
    public AirportDTO getAirport(String iataCode);
}
