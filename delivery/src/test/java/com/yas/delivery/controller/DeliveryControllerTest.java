package com.yas.delivery.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DeliveryControllerTest {

    @Test
    void testDeliveryController_ShouldNotBeNull() {
        DeliveryController deliveryController = new DeliveryController();
        assertNotNull(deliveryController);
    }
}
