package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightRouteDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;

public interface FlightsRouteService {
    List<FlightRouteDTO> getAvailableRoutes(FlightQueryDTO query, List<FlightDTO> flights);
}
