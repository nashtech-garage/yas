package com.yas.payment.paypal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CheckoutIdHelperTest {

    @Test
    void setCheckoutId_thenGetCheckoutId_shouldReturnLatestValue() {
        CheckoutIdHelper.setCheckoutId("checkout-001");
        assertEquals("checkout-001", CheckoutIdHelper.getCheckoutId());

        CheckoutIdHelper.setCheckoutId("checkout-002");
        assertEquals("checkout-002", CheckoutIdHelper.getCheckoutId());
    }
}
