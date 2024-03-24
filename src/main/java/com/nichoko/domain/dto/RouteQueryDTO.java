package com.nichoko.domain.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RouteQueryDTO {
    private String origin;
    private String destination;
    private int maxNrConnections;
}
