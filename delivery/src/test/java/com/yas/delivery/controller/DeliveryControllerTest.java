package com.yas.delivery.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RestController;

class DeliveryControllerTest {

    @Test
    void deliveryController_shouldBeRestController() {
        assertThat(DeliveryController.class.isAnnotationPresent(RestController.class)).isTrue();
    }
}
