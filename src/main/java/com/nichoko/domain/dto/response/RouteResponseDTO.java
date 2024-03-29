package com.nichoko.domain.dto.response;

import java.util.List;

import com.nichoko.domain.dto.ConnectionDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RouteResponseDTO {
    List<List<ConnectionDTO>> routes;
}
