package com.nichoko.service.interfaces;

import java.util.List;

import com.nichoko.domain.dto.ConnectionDTO;
import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.query.ConnectionQueryDTO;
import com.nichoko.domain.dto.query.FlightQueryDTO;

public interface AirlineService {
    List<FlightDTO> getCompanyFlights(FlightQueryDTO query);
    List<ConnectionDTO> getAirportConnections(ConnectionQueryDTO query);
    String getAirlineName();
}
