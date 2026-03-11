package com.yas.payment.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithValidCode_ReturnsFormattedMessage() {
        String result = MessagesUtils.getMessage(Constants.Message.SUCCESS_MESSAGE);
        assertNotNull(result);
        assertEquals("SUCCESS", result);
    }

    @Test
    void getMessage_WithPlaceholder_ReturnsFormattedMessage() {
        String result = MessagesUtils.getMessage(
                Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND, "PAYPAL");
        assertNotNull(result);
        assertTrue(result.contains("PAYPAL"));
    }

    @Test
    void getMessage_WithInvalidCode_ReturnsCodeAsMessage() {
        String invalidCode = "NON_EXISTENT_CODE_XYZ_" + System.currentTimeMillis();
        String result = MessagesUtils.getMessage(invalidCode);
        assertEquals(invalidCode, result);
    }
}
