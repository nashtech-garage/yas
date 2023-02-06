package com.yas.storefrontbff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "yas")
public record ServiceUrlConfig(
        Map<String, String> services) {
}
