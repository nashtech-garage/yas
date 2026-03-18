package com.yas.storefrontbff.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceUrlConfigTest {

    @Test
    void constructor_shouldStoreServicesMap() {
        Map<String, String> services = Map.of(
                "customer", "http://localhost:8081/customer",
                "cart", "http://localhost:8082/cart"
        );
        ServiceUrlConfig config = new ServiceUrlConfig(services);

        assertThat(config.services()).isEqualTo(services);
    }

    @Test
    void constructor_shouldAllowEmptyServicesMap() {
        ServiceUrlConfig config = new ServiceUrlConfig(Map.of());
        assertThat(config.services()).isEmpty();
    }

    @Test
    void constructor_shouldAllowNullServicesMap() {
        ServiceUrlConfig config = new ServiceUrlConfig(null);
        assertThat(config.services()).isNull();
    }

    @Test
    void services_shouldReturnCustomerUrl() {
        ServiceUrlConfig config = new ServiceUrlConfig(
                Map.of("customer", "http://api.yas.local/customer")
        );
        assertThat(config.services().get("customer")).isEqualTo("http://api.yas.local/customer");
    }

    @Test
    void services_shouldReturnCartUrl() {
        ServiceUrlConfig config = new ServiceUrlConfig(
                Map.of("cart", "http://api.yas.local/cart")
        );
        assertThat(config.services().get("cart")).isEqualTo("http://api.yas.local/cart");
    }

    @Test
    void equals_shouldReturnTrueForSameMap() {
        Map<String, String> services = Map.of("customer", "http://localhost");
        assertThat(new ServiceUrlConfig(services)).isEqualTo(new ServiceUrlConfig(services));
    }

    @Test
    void equals_shouldReturnFalseForDifferentMap() {
        assertThat(new ServiceUrlConfig(Map.of("a", "b")))
                .isNotEqualTo(new ServiceUrlConfig(Map.of("c", "d")));
    }

    @Test
    void hashCode_shouldBeConsistent() {
        Map<String, String> services = Map.of("k", "v");
        assertThat(new ServiceUrlConfig(services).hashCode())
                .isEqualTo(new ServiceUrlConfig(services).hashCode());
    }
}