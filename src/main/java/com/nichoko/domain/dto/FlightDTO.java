package com.nichoko.domain.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FlightDTO {
    Long id;
    String origin;
    String destination;
    Float price;
    LocalDateTime departureDate;
    LocalDateTime landingDate;
    LocalDateTime createdAt;
}
