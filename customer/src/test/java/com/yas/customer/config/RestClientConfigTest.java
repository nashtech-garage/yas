package com.yas.customer.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestClientConfigTest {

    @Test
    void testRestClient() {
        RestClientConfig config = new RestClientConfig();
        RestClient client = config.restClient();
        assertNotNull(client);
    }
}
