package com.nichoko.utils;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.nichoko.exception.InvalidDateException;

import java.time.LocalDate;
import java.util.List;

@QuarkusTest
class DateUtilsTest {
    @Test
    void testGetDatesRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 3, 10);
        LocalDate endDate = LocalDate.of(2024, 3, 15);

        // Act
        List<LocalDate> dates = DateUtils.getDatesRange(startDate, endDate);

        // Assert
        assertNotNull(dates);
        assertEquals(6, dates.size());
        assertEquals(startDate, dates.get(0));
        assertEquals(endDate, dates.get(dates.size() - 1));
    }

    @Test
    void testGetDatesRangeWithEndDateBeforeStartDate() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 3, 15);
        LocalDate endDate = LocalDate.of(2024, 3, 10);

        // Act & Assert
        assertThrows(InvalidDateException.class, () -> {
            DateUtils.getDatesRange(startDate, endDate);
        });
    }

    @Test
    void testConstructorThrowsException() {
        assertThrows(IllegalStateException.class, () -> {
            new DateUtils();
        });
    }

}