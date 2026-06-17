package com.yas.observability.configuration.tracing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

/**
 * Configuration class that defines beans related to context propagation for asynchronous tasks.
 * This ensures that tracing and other context information is properly propagated across thread boundaries.
 */
@Configuration(proxyBeanMethods = false)
public class ContextPropagationConfiguration {

    @Bean
    ContextPropagatingTaskDecorator contextPropagatingTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

}