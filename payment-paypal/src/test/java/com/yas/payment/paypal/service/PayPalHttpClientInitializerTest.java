package com.yas.payment.paypal.service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayPalHttpClientInitializerTest {

    private PayPalHttpClientInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new PayPalHttpClientInitializer();
    }

    @Test
    void testCreatePaypalClient_SandboxMode() {
        String settings = "{\"clientId\":\"testClient\",\"clientSecret\":\"testSecret\",\"mode\":\"sandbox\"}";
        PayPalHttpClient client = initializer.createPaypalClient(settings);
        assertNotNull(client);
    }

    @Test
    void testCreatePaypalClient_LiveMode() {
        String settings = "{\"clientId\":\"testClient\",\"clientSecret\":\"testSecret\",\"mode\":\"live\"}";
        PayPalHttpClient client = initializer.createPaypalClient(settings);
        assertNotNull(client);
    }

    @Test
    void testCreatePaypalClient_NullSettings() {
        assertThrows(IllegalArgumentException.class, () -> initializer.createPaypalClient(null));
    }
}
