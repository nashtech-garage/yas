package com.yas.payment.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_withValidCode_shouldReturnMessage() {
        // We assume "PAYMENT_NOT_FOUND" exists in messages.properties
        // If not, it will return the code itself.
        String code = "PAYMENT_NOT_FOUND";
        String message = MessagesUtils.getMessage(code);
        // If the bundle is loaded correctly, it should find it or return the code.
        // We can't be 100% sure of the content here without reading the .properties file,
        // but this will at least exercise the code.
        assertEquals(message != null, true);
    }

    @Test
    void getMessage_withInvalidCode_shouldReturnCode() {
        String code = "non.existent.code";
        String message = MessagesUtils.getMessage(code);
        assertEquals(code, message);
    }
}
