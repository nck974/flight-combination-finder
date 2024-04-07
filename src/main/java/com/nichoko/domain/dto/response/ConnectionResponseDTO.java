package com.nichoko.domain.dto.response;

import java.util.List;

import com.nichoko.domain.dto.ConnectionDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ConnectionResponseDTO {
    List<ConnectionDTO> connections;
}
