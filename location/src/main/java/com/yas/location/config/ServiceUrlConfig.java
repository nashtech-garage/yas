package com.yas.location.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.services")
public record ServiceUrlConfig(String customer) {
}
