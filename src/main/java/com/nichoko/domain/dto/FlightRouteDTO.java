package com.nichoko.domain.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FlightRouteDTO {
    Long id;
    Float price;
    LocalDateTime departureDate;
    LocalDateTime landingDate;
    int duration;
}
