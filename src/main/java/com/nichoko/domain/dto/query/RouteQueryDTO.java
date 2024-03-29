package com.nichoko.domain.dto.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class RouteQueryDTO {
    private String origin;
    private String destination;
    private int maxNrConnections;
}
