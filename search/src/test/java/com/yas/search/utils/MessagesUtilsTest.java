package com.yas.search.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenValidMessageCode_thenReturnMessage() {
        // Given
        String messageCode = "ERROR_PRODUCT_NOT_FOUND";

        // When
        String message = MessagesUtils.getMessage(messageCode);

        // Then
        assertNotNull(message);
    }

    @Test
    void testGetMessage_whenInvalidMessageCode_thenReturnMessageCode() {
        // Given
        String messageCode = "INVALID_CODE_NOT_EXISTS";

        // When
        String message = MessagesUtils.getMessage(messageCode);

        // Then
        assertEquals(messageCode, message);
    }

    @Test
    void testGetMessage_whenMessageCodeWithParameters_thenReturnFormattedMessage() {
        // Given
        String messageCode = "ERROR_TEST_WITH_PARAMS";
        String param1 = "Product";
        Long param2 = 123L;

        // When
        String message = MessagesUtils.getMessage(messageCode, param1, param2);

        // Then
        assertNotNull(message);
    }

    @Test
    void testGetMessage_whenEmptyMessageCode_thenHandleGracefully() {
        // Given
        String messageCode = "";

        // When
        String message = MessagesUtils.getMessage(messageCode);

        // Then
        assertNotNull(message);
        assertEquals("", message);
    }

    @Test
    void testGetMessage_whenNullParameter_thenHandleGracefully() {
        // Given
        String messageCode = "ERROR_TEST";
        Object param = null;

        // When
        String message = MessagesUtils.getMessage(messageCode, param);

        // Then
        assertNotNull(message);
    }
}
