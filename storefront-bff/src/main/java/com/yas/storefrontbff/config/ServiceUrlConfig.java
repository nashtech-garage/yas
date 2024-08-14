package com.yas.storefrontbff.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas")
public record ServiceUrlConfig(
    Map<String, String> services) {
}
