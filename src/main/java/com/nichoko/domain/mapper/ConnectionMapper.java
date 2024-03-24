package com.nichoko.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.nichoko.domain.dao.Connection;
import com.nichoko.domain.dto.ConnectionDTO;

@Mapper(componentModel = "jakarta", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConnectionMapper {

    public ConnectionDTO toDTO(Connection connection);

    public Connection toDAO(ConnectionDTO connectionDTO);

}
