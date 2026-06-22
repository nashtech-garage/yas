package com.yas.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

class DeliveryServiceTest {

    @Test
    void deliveryService_shouldBeServiceComponent() {
        assertThat(DeliveryService.class.isAnnotationPresent(Service.class)).isTrue();
    }
}
