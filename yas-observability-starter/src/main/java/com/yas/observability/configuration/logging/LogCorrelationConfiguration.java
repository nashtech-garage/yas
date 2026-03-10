package com.yas.observability.configuration.logging;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OpenTelemetryAppender.class)
@ConditionalOnProperty(
    name = "yas.observability.log-export-enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class LogCorrelationConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LogCorrelationConfiguration.class);

    /**
     * Installs the OTel SDK into the Logback appender once the
     * ApplicationContext is fully initialised. This bridges Logback
     * logs into the OTel pipeline (OTLP log export).
     */
    @EventListener(ApplicationReadyEvent.class)
    public void installOtelLogAppender(ApplicationReadyEvent event) {
        OpenTelemetry otel = event.getApplicationContext().getBean(OpenTelemetry.class);
        OpenTelemetryAppender.install(otel);
        log.info("OpenTelemetry Logback appender installed");
    }
}