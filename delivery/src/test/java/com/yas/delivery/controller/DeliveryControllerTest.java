package com.yas.delivery.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DeliveryControllerTest {

    @Test
    void testDeliveryController_shouldInstantiate() {
        DeliveryController controller = new DeliveryController();
        assertNotNull(controller);
    }
}
