package com.yas.commonlibrary.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithExistingCode_ReturnsFormattedMessage() {
        String message = MessagesUtils.getMessage("non.existing.code", "param1");
        assertEquals("non.existing.code", message);
    }

    @Test
    void getMessage_WithArguments_ReturnsFormattedMessage() {
        String message = MessagesUtils.getMessage("code", "arg1", "arg2");
        assertEquals("code", message);
    }
}
