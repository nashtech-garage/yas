package com.yas.storefrontbff.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ServiceUrlConfigTest {

    @Test
    void constructorAndAccessor_ShouldExposeConfiguredServiceMap() {
        Map<String, String> services = Map.of(
            "customer", "http://api.yas.local/customer",
            "cart", "http://api.yas.local/cart"
        );

        ServiceUrlConfig config = new ServiceUrlConfig(services);

        assertThat(config.services()).isEqualTo(services);
        assertThat(config.services().get("customer")).isEqualTo("http://api.yas.local/customer");
        assertThat(config.services().get("cart")).isEqualTo("http://api.yas.local/cart");
    }
}
