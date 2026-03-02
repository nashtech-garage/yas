package com.yas.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.services")
public record ServiceUrlConfig(
        String cart, String customer, String product, String tax, String promotion, String payment) {
}
