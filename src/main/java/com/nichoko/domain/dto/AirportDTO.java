package com.nichoko.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirportDTO {
    public String iataCode;
    public String icaoCode;
    public String name;
    public double latDecimal;
    public double lonDecimal;
}
