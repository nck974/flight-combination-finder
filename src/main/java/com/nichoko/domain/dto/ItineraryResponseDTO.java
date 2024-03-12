package com.nichoko.domain.dto;

import java.util.List;

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
