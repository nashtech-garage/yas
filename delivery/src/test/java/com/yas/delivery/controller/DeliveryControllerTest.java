package com.yas.delivery.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeliveryControllerTest {

    @Test
    void testControllerInstantiation() {
        DeliveryController deliveryController = new DeliveryController();
        assertNotNull(deliveryController);
    }
}
