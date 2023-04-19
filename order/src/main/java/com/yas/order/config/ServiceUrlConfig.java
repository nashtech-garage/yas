package com.yas.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.services")
public record ServiceUrlConfig(
        String cart, String product, String customer, String inventory) {
}
