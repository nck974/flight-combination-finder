package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightQueryDTO;
import com.nichoko.domain.dto.FlightRouteDTO;

public interface FlightsDetailsService {
    List<FlightDTO> setFlightsDuration(List<FlightDTO> flights);
    List<FlightRouteDTO> getItineraryOptions(FlightQueryDTO query, List<FlightDTO> flights);
}
