package com.nichoko.repository;

import com.nichoko.domain.dao.Connection;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConnectionRepository implements PanacheRepository<Connection> {

}
