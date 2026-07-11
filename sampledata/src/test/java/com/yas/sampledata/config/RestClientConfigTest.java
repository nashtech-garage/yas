package com.yas.sampledata.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestClientConfigTest {

    @Test
    void restClient_shouldCreateBean() {
        RestClientConfig config = new RestClientConfig();
        RestClient restClient = config.restClient();
        assertNotNull(restClient);
    }
}
