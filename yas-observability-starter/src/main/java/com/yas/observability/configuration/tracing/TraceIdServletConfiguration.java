package com.yas.observability.configuration.tracing;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Registers TraceIdResponseHeaderFilter only on servlet stacks.
 *
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OncePerRequestFilter.class)
@ConditionalOnWebApplication(type = SERVLET)
public class TraceIdServletConfiguration {

    @Bean
    @ConditionalOnBean(Tracer.class)
    TraceIdResponseHeaderFilter traceIdResponseHeaderFilter(Tracer tracer) {
        return new TraceIdResponseHeaderFilter(tracer);
    }
}