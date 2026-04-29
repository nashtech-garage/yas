package com.yas.order.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SwaggerConfigTest {
    @Test
    void testSwaggerConfig() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        assertNotNull(swaggerConfig);
    }
}
