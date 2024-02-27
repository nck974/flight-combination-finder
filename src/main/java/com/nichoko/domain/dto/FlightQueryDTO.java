package com.nichoko.domain.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FlightQueryDTO {
    private String origin;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
}
