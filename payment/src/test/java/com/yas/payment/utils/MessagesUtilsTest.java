package com.yas.payment.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void testGetMessage_MissingResource() {
        // Nhánh 1: Cố tình truyền Key sai để nhảy vào catch block
        String result = MessagesUtils.getMessage("NON_EXISTENT_KEY_123");
        assertEquals("NON_EXISTENT_KEY_123", result);
    }

    @Test
    void testGetMessage_WithFormatting() {
        // Nhánh 2: Truyền đúng format có parameter
        String result = MessagesUtils.getMessage("Error: {} not found", "PaymentProvider");
        assertEquals("Error: PaymentProvider not found", result);
    }
}