package com.yas.observability.configuration.tracing;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.jspecify.annotations.Nullable;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux filter that adds the current trace ID to the response headers.
 *
 * Not a Spring-managed component directly - instantiated by {@link TraceIdReactiveConfiguration},
 * which carries all @Conditional guards so this class is never loaded on servlet classpaths.
 */
public class TraceIdResponseHeaderWebFilter implements WebFilter {

    private final Tracer tracer;

    TraceIdResponseHeaderWebFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    private @Nullable String getTraceId() {
        TraceContext context = this.tracer.currentTraceContext().context();
        return context != null ? context.traceId() : null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = getTraceId();

        if (traceId != null) {
            exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
        }

        return chain.filter(exchange);
    }
}