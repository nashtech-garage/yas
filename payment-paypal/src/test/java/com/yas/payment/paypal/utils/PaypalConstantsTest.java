package com.yas.payment.paypal.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaypalConstantsTest {

    @Test
    void testConstants() {
        assertEquals("SIGN_IN_REQUIRED", Constants.ErrorCode.SIGN_IN_REQUIRED);
        assertEquals("FORBIDDEN", Constants.ErrorCode.FORBIDDEN);
        assertEquals("PAYMENT_FAIL_MESSAGE", Constants.Message.PAYMENT_FAIL_MESSAGE);
        assertEquals("PAYMENT_SUCCESS_MESSAGE", Constants.Message.PAYMENT_SUCCESS_MESSAGE);
        assertEquals("Yas", Constants.Yas.BRAND_NAME);
    }
}
