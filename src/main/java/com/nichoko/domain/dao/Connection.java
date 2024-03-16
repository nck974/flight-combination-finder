package com.nichoko.domain.dao;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
@java.lang.SuppressWarnings("java:S1104")
public class Connection extends PanacheEntity {
    public String origin;
    public String destination;

    @UpdateTimestamp
    public LocalDateTime createdAt;
}
