package com.yas.commonlibrary.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenCodeExists_ReturnsFormattedMessage() {
        String result = MessagesUtils.getMessage("PRODUCT_NOT_FOUND", 123);

        assertEquals("The product 123 is not found", result);
    }

    @Test
    void getMessage_WhenCodeMissing_ReturnsErrorCode() {
        String result = MessagesUtils.getMessage("UNKNOWN_MESSAGE_CODE");

        assertEquals("UNKNOWN_MESSAGE_CODE", result);
    }
}
