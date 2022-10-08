package com.yas.storefrontbff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@ConstructorBinding
@ConfigurationProperties(prefix = "yas")
public record ServiceUrlConfig(
        Map<String, String> services) {
}
