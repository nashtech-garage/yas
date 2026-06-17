package com.yas.observability.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.server.observation.OpenTelemetryServerRequestObservationConvention;

import static org.assertj.core.api.Assertions.assertThat;

class YasObservabilityAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(YasObservabilityAutoConfiguration.class));

    @Test
    void autoConfigIsDisabledWhenPropertyIsFalse() {
        contextRunner
            .withPropertyValues("yas.observability.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(YasObservabilityProperties.class))
            .run(context -> assertThat(context).doesNotHaveBean(OpenTelemetryServerRequestObservationConvention.class));
    }

    @Test
    void autoConfigIsEnabledByDefault() {
        contextRunner.run(context ->
            assertThat(context).hasSingleBean(YasObservabilityProperties.class));
    }
}