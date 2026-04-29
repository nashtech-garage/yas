package com.yas.rating.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestClientConfigTest {
    @Test
    void getRestClient_ShouldReturnRestClient() {
        RestClientConfig config = new RestClientConfig();
        RestClient.Builder builder = RestClient.builder();
        RestClient restClient = config.getRestClient(builder);
        assertNotNull(restClient);
    }
}
