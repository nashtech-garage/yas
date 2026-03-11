package com.yas.payment.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void errorCode_PaymentProviderNotFound_HasCorrectValue() {
        assertEquals("PAYMENT_PROVIDER_NOT_FOUND", Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
    }

    @Test
    void message_SuccessMessage_HasCorrectValue() {
        assertEquals("SUCCESS", Constants.Message.SUCCESS_MESSAGE);
    }
}
