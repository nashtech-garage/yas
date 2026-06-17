package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"hello", " hello ", "a", "test string", "123"})
    void hasText_whenHasContent_thenReturnTrue(String input) {
        assertTrue(StringUtils.hasText(input));
    }

    @Test
    void hasText_whenNull_thenReturnFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n", "  \t\n  "})
    void hasText_whenEmptyOrBlank_thenReturnFalse(String input) {
        assertFalse(StringUtils.hasText(input));
    }
}
