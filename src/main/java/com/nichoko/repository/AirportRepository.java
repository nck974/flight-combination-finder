package com.nichoko.repository;

import com.nichoko.domain.dao.Airport;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AirportRepository implements PanacheRepository<Airport> {

}
