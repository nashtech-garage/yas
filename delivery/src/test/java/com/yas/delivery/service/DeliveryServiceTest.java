package com.yas.delivery.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DeliveryServiceTest {

    @Test
    void testDeliveryService_ShouldNotBeNull() {
        DeliveryService deliveryService = new DeliveryService();
        assertNotNull(deliveryService);
    }
}
