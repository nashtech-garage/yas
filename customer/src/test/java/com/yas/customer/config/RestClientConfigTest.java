package com.yas.customer.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class RestClientConfigTest {

    @Test
    void restClient_ShouldReturnRestClientInstance() {
        RestClientConfig config = new RestClientConfig();
        RestClient restClient = config.restClient();
        assertNotNull(restClient);
    }
}
