package com.yas.observability.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.observability")
@Getter
@Setter
public class YasObservabilityProperties {

    private boolean enabled = true;

    /** Whether to install the OpenTelemetry Logback appender automatically. */
    private boolean logExportEnabled = true;

}