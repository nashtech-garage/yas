package com.yas.webhook.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void getMessage_withExistingCode_shouldReturnMessage() {
        String result = MessagesUtils.getMessage("test.code");
        assertEquals("test.code", result);
    }

    @Test
    void getMessage_withParameters_shouldFormatMessage() {
        String result = MessagesUtils.getMessage("test.{} code", "formatted");
        assertEquals("test.formatted code", result);
    }
}
