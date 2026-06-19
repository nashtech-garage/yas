package com.yas.delivery.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeliveryServiceTest {

    @Test
    void testServiceInstantiation() {
        DeliveryService deliveryService = new DeliveryService();
        assertNotNull(deliveryService);
    }
}
