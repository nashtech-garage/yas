package com.yas.customer.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessageWithValidCode() {
        String result = MessagesUtils.getMessage("WRONG_EMAIL_FORMAT", "World");
        assertEquals("Wrong email format for World", result);
    }

    @Test
    void testGetMessageWithInvalidCode() {

        String result = MessagesUtils.getMessage("invalid.code");
        assertEquals("invalid.code", result);
    }

    @Test
    void testGetMessageWithParameters() {
        // Assuming "WRONG_EMAIL_FORMAT" is "Wrong email format for {0}"
        // The test above uses "World" as parameter.
        // Let's test with multiple parameters if a message key supports it.
        // Since we don't know other keys, let's test with the existing key and multiple params
        // or just verify the formatter works.
        // Actually, the existing test `testGetMessageWithValidCode` uses one parameter.
        // Let's add a test with two parameters if possible.
        // We can use "invalid.code" which is not found, so it returns the code itself.
        // MessageFormatter.arrayFormat("invalid.code", "a", "b") -> "invalid.code a b".
        
        String result = MessagesUtils.getMessage("invalid.code", "param1", "param2");
        assertEquals("invalid.code param1 param2", result);
    }
}
