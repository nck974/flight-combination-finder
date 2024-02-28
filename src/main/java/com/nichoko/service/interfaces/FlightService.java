package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.FlightDTO;

public interface FlightService {
    public FlightDTO saveFlight(FlightDTO flightDTO);

    public List<FlightDTO> saveFlights(List<FlightDTO> flights);

    public List<FlightDTO> listSavedFlights();
}
