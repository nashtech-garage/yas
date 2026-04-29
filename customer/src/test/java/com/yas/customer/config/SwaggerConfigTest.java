package com.yas.customer.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SwaggerConfigTest {

    @Test
    void testSwaggerConfigInstantiation() {
        SwaggerConfig config = new SwaggerConfig();
        assertNotNull(config);
    }
}
