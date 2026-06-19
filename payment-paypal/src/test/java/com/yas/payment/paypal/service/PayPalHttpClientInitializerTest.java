package com.yas.payment.paypal.service;

import com.paypal.core.PayPalHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayPalHttpClientInitializerTest {

    private PayPalHttpClientInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new PayPalHttpClientInitializer();
    }

    @Test
    void createPaypalClient_withSandboxMode_shouldReturnClient() {
        // Given
        String settings = "{\"clientId\": \"test-id\", \"clientSecret\": \"test-secret\", \"mode\": \"sandbox\"}";

        // When
        PayPalHttpClient client = initializer.createPaypalClient(settings);

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void createPaypalClient_withLiveMode_shouldReturnClient() {
        // Given
        String settings = "{\"clientId\": \"live-id\", \"clientSecret\": \"live-secret\", \"mode\": \"live\"}";

        // When
        PayPalHttpClient client = initializer.createPaypalClient(settings);

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void createPaypalClient_withNullSettings_shouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            initializer.createPaypalClient(null)
        );
        assertThat(exception.getMessage()).isEqualTo("The additionalSettings can not be null.");
    }

    @ParameterizedTest
    @CsvSource({
        "'{}'", // Missing fields
        "'{\"clientId\": \"id\"}'", // Missing clientSecret and mode
        "'invalid-json'" // Invalid JSON format
    })
    void createPaypalClient_withInvalidJson_shouldThrowException(String invalidSettings) {
        // When & Then
        assertThrows(RuntimeException.class, () -> 
            initializer.createPaypalClient(invalidSettings)
        );
    }
}
