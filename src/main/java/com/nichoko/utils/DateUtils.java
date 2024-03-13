package com.nichoko.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nichoko.exception.InvalidDateException;

public class DateUtils {

    DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<LocalDate> getDatesRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateException("End date is before the start date", 1000);
        }

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return dates;
    }

}
