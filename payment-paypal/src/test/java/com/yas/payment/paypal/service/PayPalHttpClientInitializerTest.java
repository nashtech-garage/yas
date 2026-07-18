package com.yas.payment.paypal.service;

import com.paypal.core.PayPalHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayPalHttpClientInitializerTest {

    private final PayPalHttpClientInitializer initializer = new PayPalHttpClientInitializer();

    @Test
    void createPaypalClientWithSandboxSettingsReturnsClient() {
        PayPalHttpClient client = initializer.createPaypalClient(
                "{\"clientId\":\"sandbox-client\",\"clientSecret\":\"sandbox-secret\",\"mode\":\"sandbox\"}");

        assertNotNull(client);
    }

    @Test
    void createPaypalClientWithLiveSettingsReturnsClient() {
        PayPalHttpClient client = initializer.createPaypalClient(
                "{\"clientId\":\"live-client\",\"clientSecret\":\"live-secret\",\"mode\":\"live\"}");

        assertNotNull(client);
    }

    @Test
    void createPaypalClientWithNullSettingsThrows() {
        assertThrows(IllegalArgumentException.class, () -> initializer.createPaypalClient(null));
    }
}
