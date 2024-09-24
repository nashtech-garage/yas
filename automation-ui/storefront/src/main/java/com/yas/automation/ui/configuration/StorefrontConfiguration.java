package com.yas.automation.ui.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storefront")
public record StorefrontConfiguration(String url, String username, String password) {}
