package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenCodeExists_thenFormatMessage() {
        String message = MessagesUtils.getMessage("COUNTRY_NOT_FOUND", "VN");

        assertEquals("The country VN is not found", message);
    }

    @Test
    void testGetMessage_whenCodeMissing_thenReturnCode() {
        String message = MessagesUtils.getMessage("UNKNOWN_ERROR_CODE");

        assertEquals("UNKNOWN_ERROR_CODE", message);
    }
}
