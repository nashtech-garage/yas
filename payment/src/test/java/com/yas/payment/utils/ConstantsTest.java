package com.yas.payment.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void testErrorCodeConstants() {
        assertEquals("PAYMENT_PROVIDER_NOT_FOUND", Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
    }

    @Test
    void testMessageConstants() {
        assertEquals("SUCCESS", Constants.Message.SUCCESS_MESSAGE);
    }

    @Test
    void testConstantsAreNotNull() {
        assertNotNull(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
        assertNotNull(Constants.Message.SUCCESS_MESSAGE);
    }
}
