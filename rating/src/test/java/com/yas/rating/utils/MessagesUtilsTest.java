package com.yas.rating.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MessagesUtilsTest {

    @Test
    void testGetMessage_existingCode() {
        // Just mocking behavior if bundle is absent
        String errorCode = "SOME_ERROR_CODE";
        String result = MessagesUtils.getMessage(errorCode);
        assertEquals(errorCode, result);
    }

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<MessagesUtils> constructor = MessagesUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
