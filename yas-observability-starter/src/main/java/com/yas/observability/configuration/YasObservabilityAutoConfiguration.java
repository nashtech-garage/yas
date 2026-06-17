package com.yas.observability.configuration;

import com.yas.observability.configuration.logging.LogCorrelationConfiguration;
import com.yas.observability.configuration.tracing.ContextPropagationConfiguration;
import com.yas.observability.configuration.tracing.TraceIdReactiveConfiguration;
import com.yas.observability.configuration.tracing.TraceIdServletConfiguration;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@ConditionalOnClass(OpenTelemetry.class)
@ConditionalOnProperty(
    name = "yas.observability.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(YasObservabilityProperties.class)
@PropertySource(
    value = "classpath:application-observability-defaults.yml",
    factory = YamlPropertySourceFactory.class
)
@Import({
    LogCorrelationConfiguration.class,
    TraceIdServletConfiguration.class,
    TraceIdReactiveConfiguration.class,
    ContextPropagationConfiguration.class,
    OpenTelemetryConventionConfiguration.class,
})
public class YasObservabilityAutoConfiguration {
}