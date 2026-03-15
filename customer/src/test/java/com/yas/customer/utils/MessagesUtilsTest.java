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
    void testGetMessageWithMultipleArguments() {
        String result = MessagesUtils.getMessage("WRONG_EMAIL_FORMAT", "val1", "val2");
        assertEquals("Wrong email format for val1", result);
    }
}
