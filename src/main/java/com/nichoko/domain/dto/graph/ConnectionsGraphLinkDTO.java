package com.nichoko.domain.dto.graph;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ConnectionsGraphLinkDTO {
    Long source;
    Long target;
    String routeName;
}
