package com.nichoko.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.nichoko.domain.dao.Connection;
import com.nichoko.domain.dto.ConnectionDTO;

import jakarta.enterprise.context.ApplicationScoped;

@Mapper(componentModel = "jakarta", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@ApplicationScoped
public interface ConnectionMapper {

    public ConnectionDTO toDTO(Connection connection);

    public Connection toDAO(ConnectionDTO connectionDTO);

}
