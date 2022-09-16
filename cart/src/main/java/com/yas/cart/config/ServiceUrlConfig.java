package com.yas.cart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "yas.services")
public record ServiceUrlConfig(
        String media) {
}
