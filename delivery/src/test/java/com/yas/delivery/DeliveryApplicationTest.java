package com.yas.delivery;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class DeliveryApplicationTest {

    @Test
    void deliveryApplication_shouldBeSpringBootApplication() {
        assertThat(DeliveryApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    }
}
