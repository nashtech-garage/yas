package com.yas.media;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.media.utils.StringUtils;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_null_returnsFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_empty_returnsFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_blank_returnsFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_text_returnsTrue() {
        assertTrue(StringUtils.hasText("hello"));
    }
}
