package com.yas.rating.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.services")
public record ServiceUrlConfig(
        String product, String customer, String order) {
}
