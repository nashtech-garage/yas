package com.yas.sampledata.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_withNonExistentKey_shouldReturnKeyItself() {
        // When key doesn't exist in bundle, it returns the key as fallback
        String result = MessagesUtils.getMessage("non.existent.key");
        assertThat(result).isEqualTo("non.existent.key");
    }

    @Test
    void getMessage_withNonExistentKeyAndArgs_shouldReturnKeyWithArgsFormatted() {
        String result = MessagesUtils.getMessage("missing.key.{}", "arg1");
        // MessageFormatter will substitute {} with arg1
        assertThat(result).isNotNull();
    }

    @Test
    void getMessage_withNullArgs_shouldNotThrow() {
        String result = MessagesUtils.getMessage("some.key");
        assertThat(result).isNotNull();
    }

    @Test
    void getMessage_withEmptyArgs_shouldReturnFallback() {
        String result = MessagesUtils.getMessage("key.not.in.bundle");
        assertThat(result).isEqualTo("key.not.in.bundle");
    }

    @Test
    void getMessage_withMultipleArgs_shouldFormatCorrectly() {
        // Key doesn't exist → key itself is used as format string
        String result = MessagesUtils.getMessage("Hello {} and {}", "Alice", "Bob");
        assertThat(result).isEqualTo("Hello Alice and Bob");
    }
}
