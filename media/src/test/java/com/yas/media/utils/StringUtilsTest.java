package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_whenNull_thenReturnFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_whenEmpty_thenReturnFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_whenBlank_thenReturnFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_whenHasContent_thenReturnTrue() {
        assertTrue(StringUtils.hasText("hello"));
    }

    @Test
    void hasText_whenHasContentWithSpaces_thenReturnTrue() {
        assertTrue(StringUtils.hasText("  hello  "));
    }
}
