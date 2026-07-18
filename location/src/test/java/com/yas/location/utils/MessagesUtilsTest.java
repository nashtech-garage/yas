package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_whenKeyExists_substitutesArgs() {
        String message = MessagesUtils.getMessage(Constants.ErrorCode.COUNTRY_NOT_FOUND, "VN");
        assertTrue(message.contains("VN"));
    }

    @Test
    void getMessage_whenKeyMissing_returnsCodeAsMessage() {
        String unknown = "__UNDEFINED_MESSAGE_KEY_XYZ__";
        assertEquals(unknown, MessagesUtils.getMessage(unknown));
    }
}
