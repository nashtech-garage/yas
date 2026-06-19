package com.yas.rating.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void getMessage_whenCodeExists_thenReturnMessage() {
        String result = MessagesUtils.getMessage("NON_EXISTENT_CODE");
        assertEquals("NON_EXISTENT_CODE", result);
    }

    @Test
    void getMessage_withArguments_thenFormatsCorrectly() {
        String result = MessagesUtils.getMessage("ERROR_WITH_{}_AND_{}", "ARG1", "ARG2");
        assertEquals("ERROR_WITH_ARG1_AND_ARG2", result);
    }
}
