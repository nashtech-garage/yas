package com.yas.observability.configuration.tracing;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

/**
 * Registers TraceIdResponseHeaderWebFilter only on reactive (WebFlux) stacks.
 *
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WebFilter.class)
@ConditionalOnWebApplication(type = REACTIVE)
public class TraceIdReactiveConfiguration {

    @Bean
    @ConditionalOnBean(Tracer.class)
    TraceIdResponseHeaderWebFilter traceIdResponseHeaderWebFilter(Tracer tracer) {
        return new TraceIdResponseHeaderWebFilter(tracer);
    }
}