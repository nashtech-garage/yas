package com.yas.automation.ui.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backoffice")
public record BackOfficeConfiguration(String url, String username, String password) {}
