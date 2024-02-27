package com.nichoko.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.nichoko.domain.dao.Flight;
import com.nichoko.domain.dto.FlightDTO;

import jakarta.enterprise.context.ApplicationScoped;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@ApplicationScoped
public interface FlightMapper {

    public FlightDTO toDTO(Flight flight);

    public Flight toDAO(FlightDTO flightDTO);

}
