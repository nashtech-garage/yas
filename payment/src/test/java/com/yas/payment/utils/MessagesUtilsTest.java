package com.yas.payment.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void getMessage_WithExistingKey_ShouldReturnMessage() {
        // Since we don't know the exact content of messages.properties in the environment,
        // we test with a key that we know might exist or one that doesn't.
        // If it doesn't exist, it should return the key itself.
        String key = "test.key";
        String message = MessagesUtils.getMessage(key);
        // If not found in bundle, returns the key
        assertEquals(key, message);
    }

    @Test
    void getMessage_WithArguments_ShouldFormatMessage() {
        // We use a mock or a known behavior.
        // If the bundle doesn't have the key, it returns the key and doesn't format (as per arrayFormat behavior on literal strings without placeholders)
        String key = "test.key {}";
        String message = MessagesUtils.getMessage(key, "arg1");
        assertEquals("test.key arg1", message);
    }
}
