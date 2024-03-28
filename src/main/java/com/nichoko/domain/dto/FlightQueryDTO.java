package com.nichoko.domain.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class FlightQueryDTO {
    private List<RouteCombination> routes;
    private LocalDate startDate;
    private LocalDate endDate;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RouteCombination {
        private String origin;
        private String destination;

        public String getRouteName() {
            return this.getOrigin() + "-" + this.getDestination();
        }
    }

    public String getRoutesCombinations() {
        StringBuilder routeStringBuilder = new StringBuilder();
        for (RouteCombination route : this.routes) {
            routeStringBuilder.append(route.getOrigin() + " -> " + route.getDestination() + "\n");
        }
        return routeStringBuilder.toString();
    }
}
