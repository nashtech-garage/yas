package com.yas.payment.paypal.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentProviderHelperTest {

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<PaymentProviderHelper> constructor = PaymentProviderHelper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
