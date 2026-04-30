package com.yas.rating.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenCodeExists_ReturnsMessage() {
        // SUCCESS message in messages.properties
        String message = MessagesUtils.getMessage("SUCCESS");
        assertEquals("SUCCESS", message);
    }

    @Test
    void getMessage_WhenCodeDoesNotExist_ReturnsErrorCode() {
        String message = MessagesUtils.getMessage("NON_EXISTENT_CODE");
        assertEquals("NON_EXISTENT_CODE", message);
    }

    @Test
    void getMessage_WithArguments() {
        String message = MessagesUtils.getMessage("NON_EXISTENT_CODE {}", "arg");
        assertEquals("NON_EXISTENT_CODE arg", message);
    }

    @Test
    void testConstructor() {
        org.junit.jupiter.api.Assertions.assertNotNull(new MessagesUtils());
    }
}
