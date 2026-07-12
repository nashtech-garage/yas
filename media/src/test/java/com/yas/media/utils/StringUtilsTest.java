package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_whenNull_thenFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_whenBlank_thenFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_whenContentExists_thenTrue() {
        assertTrue(StringUtils.hasText("yas"));
    }
}
