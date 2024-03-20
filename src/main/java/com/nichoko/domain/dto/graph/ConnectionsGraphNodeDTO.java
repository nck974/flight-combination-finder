package com.nichoko.domain.dto.graph;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ConnectionsGraphNodeDTO {
    Long id;
    String name;
    String completeName;
    Float symbolSize;
    Float value;
    Long category;
    Double x;
    Double y;
}
