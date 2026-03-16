package com.yas.storefrontbff.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ServiceUrlConfigTest {

    @Test
    void recordHoldsConfiguredServices() {
        ServiceUrlConfig serviceUrlConfig = new ServiceUrlConfig(Map.of("cart", "http://cart", "customer", "http://customer"));

        assertEquals("http://cart", serviceUrlConfig.services().get("cart"));
        assertEquals("http://customer", serviceUrlConfig.services().get("customer"));
    }
}
