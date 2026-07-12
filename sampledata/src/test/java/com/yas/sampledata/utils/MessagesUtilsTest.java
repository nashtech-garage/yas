package com.yas.sampledata.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_ValidErrorCode_ShouldReturnMessage() {
        String message = MessagesUtils.getMessage("NON_EXISTENT_CODE");
        assertEquals("NON_EXISTENT_CODE", message);

        String formattedMessage = MessagesUtils.getMessage("NON_EXISTENT_CODE {}", "Arg1");
        assertEquals("NON_EXISTENT_CODE Arg1", formattedMessage);
    }
}
