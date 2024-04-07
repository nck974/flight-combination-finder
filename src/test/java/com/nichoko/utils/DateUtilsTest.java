package com.nichoko.utils;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.nichoko.exception.InvalidDateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Test
    void testCalculateFlightDuration__normal() {
        assertEquals(3, DateUtils.calculateFlightDuration(
                LocalDateTime.of(2024, 3, 13, 10, 0),
                LocalDateTime.of(2024, 3, 13, 12, 30)));
    }

    @Test
    void testCalculateFlightDuration__twoDays() {
        assertEquals(11, DateUtils.calculateFlightDuration(
                LocalDateTime.of(2024, 3, 13, 15, 0),
                LocalDateTime.of(2024, 3, 14, 1, 30)));
    }

    @Test
    void testCalculateFlightDuration__sameHour() {
        assertEquals(1, DateUtils.calculateFlightDuration(
                LocalDateTime.of(2024, 3, 13, 15, 0),
                LocalDateTime.of(2024, 3, 13, 15, 30)));
    }
    @Test
    void testCalculateFlightDuration__zeroMinuteLandingDate() {
        assertEquals(1, DateUtils.calculateFlightDuration(
                LocalDateTime.of(2024, 3, 13, 14, 0),
                LocalDateTime.of(2024, 3, 13, 15, 0)));
    }

}