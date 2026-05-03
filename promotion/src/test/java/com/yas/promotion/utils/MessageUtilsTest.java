package com.yas.promotion.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void testGetMessage_MissingResource() {
        String result = MessagesUtils.getMessage("NON_EXISTENT_KEY_123");
        assertEquals("NON_EXISTENT_KEY_123", result);
    }

    @Test
    void testGetMessage_WithFormatting() {
        String result = MessagesUtils.getMessage("Error: {} not found", "Promotion");
        assertEquals("Error: Promotion not found", result);
    }
}