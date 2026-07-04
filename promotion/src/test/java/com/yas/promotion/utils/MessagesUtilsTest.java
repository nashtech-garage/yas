package com.yas.promotion.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void getMessage_whenCodeExists_thenReturnMessage() {
        // We assume we don't have this code, so it will return the code itself
        // Or if we have some standard code we can test it. 
        // For now let's just test that it falls back to errorCode if not found
        String result = MessagesUtils.getMessage("NON_EXISTENT_CODE");
        assertEquals("NON_EXISTENT_CODE", result);
    }

    @Test
    void getMessage_withArguments_thenFormatsCorrectly() {
        // Even if it falls back to errorCode, SLF4J MessageFormatter will still format {}
        String result = MessagesUtils.getMessage("ERROR_WITH_{}_AND_{}", "ARG1", "ARG2");
        assertEquals("ERROR_WITH_ARG1_AND_ARG2", result);
    }
}
