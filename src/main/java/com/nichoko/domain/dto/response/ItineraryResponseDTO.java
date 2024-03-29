package com.nichoko.domain.dto.response;

import java.util.List;

import com.nichoko.domain.dto.FlightDTO;
import com.nichoko.domain.dto.FlightRouteDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ItineraryResponseDTO {
    List<FlightDTO> flights;
    List<FlightRouteDTO> availableRoutes;
}
