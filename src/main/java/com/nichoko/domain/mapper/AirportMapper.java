package com.nichoko.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.nichoko.domain.dao.Airport;
import com.nichoko.domain.dto.AirportDTO;

@Mapper(componentModel = "jakarta", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AirportMapper {

    public AirportDTO toDTO(Airport airport);

    public Airport toDAO(AirportDTO airportDTO);

}
