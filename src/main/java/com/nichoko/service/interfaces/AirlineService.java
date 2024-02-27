package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightQueryDTO;

public interface AirlineService {
    List<FlightDTO> getCompanyFlights(FlightQueryDTO query);
}
