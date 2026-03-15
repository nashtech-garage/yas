package com.yas.payment.paypal.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CheckoutIdHelperTest {

    @Test
    void testGetAndSetCheckoutId() {
        CheckoutIdHelper.setCheckoutId("test1234");
        assertEquals("test1234", CheckoutIdHelper.getCheckoutId());
    }

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<CheckoutIdHelper> constructor = CheckoutIdHelper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
