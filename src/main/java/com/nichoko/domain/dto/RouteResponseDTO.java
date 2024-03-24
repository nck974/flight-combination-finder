package com.nichoko.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RouteResponseDTO {
    List<List<ConnectionDTO>> routes;
}
