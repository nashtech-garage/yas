package com.yas.media.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testHasText_WithNull() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void testHasText_WithEmptyString() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void testHasText_WithBlankString() {
        // Chuỗi chỉ chứa khoảng trắng
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void testHasText_WithValidText() {
        assertTrue(StringUtils.hasText("yas-media"));
        assertTrue(StringUtils.hasText("  yas-media  "));
    }
}