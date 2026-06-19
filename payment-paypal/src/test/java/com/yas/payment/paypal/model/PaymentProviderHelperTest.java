package com.yas.payment.paypal.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentProviderHelperTest {

    @Test
    void testConstants() {
        assertEquals("PAYPAL", PaymentProviderHelper.PAYPAL_PAYMENT_PROVIDER_ID);
    }
}
