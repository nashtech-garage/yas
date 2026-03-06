package com.yas.cart.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

class RestClientConfigTest {

    @Test
    void testRestClient_shouldReturnNonNullRestClient() {
        // Given
        RestClientConfig restClientConfig = new RestClientConfig();

        // When
        RestClient restClient = restClientConfig.restClient();

        // Then
        assertNotNull(restClient);
    }

    @Test
    void testRestClient_shouldReturnValidRestClientInstance() {
        // Given
        RestClientConfig restClientConfig = new RestClientConfig();

        // When
        RestClient restClient = restClientConfig.restClient();

        // Then
        assertNotNull(restClient);
        assertTrue(restClient instanceof RestClient);
    }
}
