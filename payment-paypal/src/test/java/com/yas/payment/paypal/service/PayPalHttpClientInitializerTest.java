package com.yas.payment.paypal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.paypal.core.PayPalHttpClient;
import static org.junit.jupiter.api.Assertions.*;

class PayPalHttpClientInitializerTest {

    private PayPalHttpClientInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new PayPalHttpClientInitializer();
    }

    @Test
    void createPaypalClient_withSandboxMode_shouldReturnClient() {
        String settings = "{\"clientId\": \"test-id\", \"clientSecret\": \"test-secret\", \"mode\": \"sandbox\"}";
        PayPalHttpClient client = initializer.createPaypalClient(settings);
        assertNotNull(client);
    }

    @Test
    void createPaypalClient_withLiveMode_shouldReturnClient() {
        String settings = "{\"clientId\": \"test-id\", \"clientSecret\": \"test-secret\", \"mode\": \"live\"}";
        PayPalHttpClient client = initializer.createPaypalClient(settings);
        assertNotNull(client);
    }

    @Test
    void createPaypalClient_withNullSettings_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> initializer.createPaypalClient(null));
    }

    @Test
    void createPaypalClient_withInvalidJson_shouldThrowException() {
        String invalidJson = "{invalid}";
        assertThrows(Exception.class, () -> initializer.createPaypalClient(invalidJson));
    }
}
