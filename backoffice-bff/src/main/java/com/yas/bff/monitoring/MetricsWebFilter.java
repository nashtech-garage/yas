package com.yas.bff.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Web filter to intercept and monitor API requests
 * Collects metrics for all incoming requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsWebFilter implements WebFilter {

    private final MetricsCollector metricsCollector;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethodValue();

        return chain.filter(exchange)
                .doFinally(signal -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 500;

                    // Record metrics
                    metricsCollector.recordRequest(
                            method + " " + path,
                            statusCode,
                            duration
                    );

                    // Log request details
                    log.info("Request: {} {} - Status: {} - Duration: {}ms",
                            method, path, statusCode, duration);

                    // Alert on high latency
                    if (duration > 1000) {
                        log.warn("High latency detected: {} {} took {}ms",
                                method, path, duration);
                    }

                    // Alert on errors
                    if (statusCode >= 500) {
                        log.error("Server error: {} {} returned status {}", 
                                method, path, statusCode);
                    }
                });
    }
}
