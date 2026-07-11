package com.yas.search.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_whenCodeNotFound_returnCode() {
        String code = "non.existent.code";
        String actualMessage = MessagesUtils.getMessage(code);
        assertEquals(code, actualMessage);
    }

    @Test
    void getMessage_withArgs_returnFormattedMessage() {
        // Since we can't easily mock ResourceBundle static field without PowerMock/MockedStatic,
        // we'll test the formatting logic which is always active.
        // If a code is not found, it returns the code itself.
        // MessageFormatter.arrayFormat("code", ["arg"]) returns "code" if no placeholders.
        String code = "test {}";
        String actualMessage = MessagesUtils.getMessage(code, "arg1");
        assertEquals("test arg1", actualMessage);
    }
}
