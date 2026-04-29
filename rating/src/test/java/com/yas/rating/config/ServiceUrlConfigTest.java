package com.yas.rating.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceUrlConfigTest {
    @Test
    void testRecordMethods() {
        ServiceUrlConfig config = new ServiceUrlConfig("productUrl", "customerUrl", "orderUrl");
        assertEquals("productUrl", config.product());
        assertEquals("customerUrl", config.customer());
        assertEquals("orderUrl", config.order());
    }
}
