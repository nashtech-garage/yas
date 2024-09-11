package com.yas.paymentpaypal.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_existingCode() {

        String errorCode = "PAYMENT_SUCCESS_MESSAGE";
        String result = MessagesUtils.getMessage(errorCode);
        assertEquals("Payment successful", result);
    }

    @Test
    void testGetMessage_nonExistingCode() {

        String errorCode = "non.existing.code";
        String result = MessagesUtils.getMessage(errorCode);
        assertEquals(errorCode, result);
    }

}