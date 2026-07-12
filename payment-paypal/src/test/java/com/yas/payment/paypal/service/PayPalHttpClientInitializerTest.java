package com.yas.payment.paypal.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PayPalHttpClientInitializerTest {

    private final PayPalHttpClientInitializer initializer = new PayPalHttpClientInitializer();

    @Test
    void createPaypalClient_whenSandboxMode_thenReturnClient() {
        String settings = "{\"clientId\":\"abc\",\"clientSecret\":\"123\",\"mode\":\"sandbox\"}";

        assertNotNull(initializer.createPaypalClient(settings));
    }

    @Test
    void createPaypalClient_whenLiveMode_thenReturnClient() {
        String settings = "{\"clientId\":\"abc\",\"clientSecret\":\"123\",\"mode\":\"live\"}";

        assertNotNull(initializer.createPaypalClient(settings));
    }

    @Test
    void createPaypalClient_whenAdditionalSettingsIsNull_thenThrow() {
        assertThrows(IllegalArgumentException.class, () -> initializer.createPaypalClient(null));
    }
}
