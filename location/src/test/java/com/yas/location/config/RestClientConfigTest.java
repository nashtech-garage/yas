package com.yas.location.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.location.LocationApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest(classes = LocationApplication.class)
class RestClientConfigTest {

    @Autowired
    private RestClient restClient;

    @Test
    void testRestClientBean_IsNotNull() {
        assertNotNull(restClient);
    }

    @Test
    void testRestClientBean_CanBeUsed() {
        assertNotNull(restClient);
        // RestClient should be initialized and ready to use
        final RestClient.RequestHeadersUriSpec<?> spec = restClient.get();
        assertNotNull(spec);
    }

    @Test
    void testRestClientConfigExists() {
        RestClientConfig config = new RestClientConfig();
        assertNotNull(config);
    }

    @Test
    void testRestClientInjected() {
        assertNotNull(restClient);
    }
}
