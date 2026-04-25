package com.yas.search.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenMessageCodeExists_thenFormatMessage() {
        String message = MessagesUtils.getMessage("PRODUCT_NOT_FOUND", "P01");

        assertEquals("The product P01 is not found", message);
    }

    @Test
    void testGetMessage_whenMessageCodeMissing_thenReturnCode() {
        String message = MessagesUtils.getMessage("UNKNOWN_ERROR_CODE");

        assertEquals("UNKNOWN_ERROR_CODE", message);
    }
}
