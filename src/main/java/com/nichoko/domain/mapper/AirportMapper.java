package com.nichoko.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.nichoko.domain.dao.Airport;
import com.nichoko.domain.dto.AirportDTO;

import jakarta.enterprise.context.ApplicationScoped;

@Mapper(componentModel = "jakarta", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@ApplicationScoped
public interface AirportMapper {

    public AirportDTO toDTO(Airport airport);

    public Airport toDAO(AirportDTO airportDTO);

}
