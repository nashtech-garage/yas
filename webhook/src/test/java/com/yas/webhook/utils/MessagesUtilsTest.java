package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_withInvalidCode_shouldReturnCode() {
        String code = "non.existent.code";
        String message = MessagesUtils.getMessage(code);
        assertEquals(code, message);
    }
}
