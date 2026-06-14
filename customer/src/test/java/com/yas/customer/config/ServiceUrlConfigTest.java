package com.yas.customer.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ServiceUrlConfigTest {

    @Test
    void testRecordProperties() {
        ServiceUrlConfig config = new ServiceUrlConfig("http://location-service");
        assertEquals("http://location-service", config.location());
    }
}
