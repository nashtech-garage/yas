package com.yas.payment.paypal.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutIdHelperTest {

    @Test
    void testSetAndGetCheckoutId() {
        String id = "test-id";
        CheckoutIdHelper.setCheckoutId(id);
        assertEquals(id, CheckoutIdHelper.getCheckoutId());
    }
}
