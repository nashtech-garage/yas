package com.yas.commonlibrary.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateTimeUtilsTest {

    @Test
    void testFormatWithDefaultPattern() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 1, 14, 30, 45);

        // When
        String result = DateTimeUtils.format(dateTime);

        // Then
        assertEquals("01-03-2026_14-30-45", result);
    }

    @Test
    void testFormatWithCustomPattern() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 1, 14, 30, 45);
        String pattern = "yyyy-MM-dd HH:mm:ss";

        // When
        String result = DateTimeUtils.format(dateTime, pattern);

        // Then
        assertEquals("2026-03-01 14:30:45", result);
    }

    @Test
    void testFormatWithDifferentPattern() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2026, 12, 25, 9, 5, 30);
        String pattern = "dd/MM/yyyy";

        // When
        String result = DateTimeUtils.format(dateTime, pattern);

        // Then
        assertEquals("25/12/2026", result);
    }

    @Test
    void testFormatWithTimeOnlyPattern() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 1, 23, 59, 59);
        String pattern = "HH:mm:ss";

        // When
        String result = DateTimeUtils.format(dateTime, pattern);

        // Then
        assertEquals("23:59:59", result);
    }

    @Test
    void testFormatWithEdgeCaseMinDateTime() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0);

        // When
        String result = DateTimeUtils.format(dateTime);

        // Then
        assertEquals("01-01-2000_00-00-00", result);
    }
}
