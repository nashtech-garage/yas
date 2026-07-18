package com.yas.rating.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_withInvalidCode_shouldReturnCode() {
        String code = "non.existent.code";
        String message = MessagesUtils.getMessage(code);
        assertEquals(code, message);
    }

    @Test
    void getMessage_whenKeyExists_thenFormatsArguments() {
        assertEquals("RATING 7 is not found", MessagesUtils.getMessage("RATING_NOT_FOUND", 7));
    }

    @Test
    void getMessage_whenKeyDoesNotExist_thenReturnsCode() {
        assertEquals("UNKNOWN_CODE", MessagesUtils.getMessage("UNKNOWN_CODE"));
    }
}
