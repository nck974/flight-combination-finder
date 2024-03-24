package com.nichoko.domain.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Airport extends PanacheEntity {
    @Column(name="icao_code")
    public String icaoCode;
    @Column(name="iata_code")
    public String iataCode;
    public String name;
    public String city;
    public String country;
    @Column(name="lat_deg")
    public int latDeg;
    @Column(name="lat_min")
    public int latMin;
    @Column(name="lat_sec")
    public int latSec;
    @Column(name="lat_dir")
    public char latDir;
    @Column(name="lon_deg")
    public int lonDeg;
    @Column(name="lon_min")
    public int lonMin;
    @Column(name="lon_sec")
    public int lonSec;
    @Column(name="lon_dir")
    public char lonDir;
    public int altitude;
    @Column(name="lat_decimal")
    public double latDecimal;
    @Column(name="lon_decimal")
    public double lonDecimal;
}