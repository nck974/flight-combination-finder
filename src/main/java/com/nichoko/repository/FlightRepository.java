package com.nichoko.repository;

import com.nichoko.domain.dao.Flight;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlightRepository implements PanacheRepository<Flight> {

}
