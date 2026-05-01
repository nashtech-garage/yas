package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_whenStringIsNull_thenReturnFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_whenStringIsEmpty_thenReturnFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_whenStringIsBlank_thenReturnFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_whenStringHasText_thenReturnTrue() {
        assertTrue(StringUtils.hasText("test"));
        assertTrue(StringUtils.hasText("  test  "));
    }
}
