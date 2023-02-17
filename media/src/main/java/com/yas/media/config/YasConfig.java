package com.yas.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas")
public record YasConfig(String publicUrl) {
}
