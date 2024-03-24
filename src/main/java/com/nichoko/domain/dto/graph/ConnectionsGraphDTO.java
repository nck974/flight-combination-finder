package com.nichoko.domain.dto.graph;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ConnectionsGraphDTO {
    List<ConnectionsGraphNodeDTO> nodes;
    List<ConnectionsGraphLinkDTO> links;
    List<ConnectionsGraphCategoryDTO> categories;
}
