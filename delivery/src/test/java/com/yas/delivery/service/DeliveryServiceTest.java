package com.yas.delivery.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DeliveryServiceTest {

    @Test
    void testGetStatus() {
        DeliveryService deliveryService = new DeliveryService();
        String status = deliveryService.getStatus();
        assertNotNull(status);
        assertThat(status).isEqualTo("Delivery Service is up and running");
    }
}
