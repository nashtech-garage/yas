package com.yas.sampledata.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SwaggerConfigTest {
    @Test
    void testSwaggerConfig() {
        SwaggerConfig config = new SwaggerConfig();
        assertNotNull(config);
    }
}
