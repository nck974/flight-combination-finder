package com.nichoko.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nichoko.exception.InvalidDateException;

public class DateUtils {

    DateUtils() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    public static List<LocalDate> getDatesRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateException();
        }

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return dates;
    }

    public static int calculateFlightDuration(LocalDateTime departureDateTime, LocalDateTime landingDateTime) {
        int departureHour = departureDateTime.getHour();
        int landingHour = landingDateTime.getHour();

        if (landingDateTime.getMinute() != 0) {
            landingHour += 1;
        }
        if (landingDateTime.getDayOfMonth() != departureDateTime.getDayOfMonth()) {
            landingHour += 24;
        }
        return landingHour - departureHour;
    }

}
