package com.yas.payment.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenCodeNotExist_returnCodeAsMessage() {
        String code = "UNKNOWN_CODE_THAT_DOES_NOT_EXIST";
        String result = MessagesUtils.getMessage(code);
        assertEquals(code, result);
    }

    @Test
    void testGetMessage_withArgs_shouldFormatCorrectly() {
        // use an unknown code so it falls back to returning the code itself
        String result = MessagesUtils.getMessage("some.code {}", "value");
        assertEquals("some.code value", result);
    }

    @Test
    void testPrivateConstructor() throws Exception {
        var constructor = MessagesUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
