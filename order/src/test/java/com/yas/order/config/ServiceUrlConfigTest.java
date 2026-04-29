package com.yas.order.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceUrlConfigTest {
    @Test
    void testRecordMethods() {
        ServiceUrlConfig config = new ServiceUrlConfig("cartUrl", "customerUrl", "productUrl", "taxUrl", "promotionUrl");
        assertEquals("cartUrl", config.cart());
        assertEquals("customerUrl", config.customer());
        assertEquals("productUrl", config.product());
        assertEquals("taxUrl", config.tax());
        assertEquals("promotionUrl", config.promotion());
    }
}
