package com.yas.payment.paypal.model;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import static org.assertj.core.api.Assertions.assertThat;

class PaymentProviderHelperTest {

    @Test
    void testConstant() {
        assertThat(PaymentProviderHelper.PAYPAL_PAYMENT_PROVIDER_ID).isEqualTo("PAYPAL");
    }

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<PaymentProviderHelper> constructor = PaymentProviderHelper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        PaymentProviderHelper instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
