package com.yas.bff.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Custom metrics collector for BFF monitoring
 * Tracks API gateway performance, latency, and error rates
 */
@Component
@RequiredArgsConstructor
public class MetricsCollector {

    private final MeterRegistry meterRegistry;

    // Counters
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    // Timers
    private final Timer requestLatency;
    private final Timer downstreamLatency;

    public MetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Initialize counters
        this.requestCounter = Counter.builder("bff.requests.total")
                .description("Total number of API requests")
                .tag("service", "backoffice-bff")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("bff.requests.errors")
                .description("Total number of API errors")
                .tag("service", "backoffice-bff")
                .register(meterRegistry);

        this.cacheHitCounter = Counter.builder("bff.cache.hits")
                .description("Cache hit count")
                .tag("service", "backoffice-bff")
                .register(meterRegistry);

        this.cacheMissCounter = Counter.builder("bff.cache.misses")
                .description("Cache miss count")
                .tag("service", "backoffice-bff")
                .register(meterRegistry);

        // Initialize timers
        this.requestLatency = Timer.builder("bff.request.latency")
                .description("API request latency in milliseconds")
                .tag("service", "backoffice-bff")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        this.downstreamLatency = Timer.builder("bff.downstream.latency")
                .description("Downstream service latency")
                .tag("service", "backoffice-bff")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    /**
     * Record API request
     */
    public void recordRequest(String endpoint, int statusCode, long duration) {
        requestCounter.increment();
        requestLatency.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);

        if (statusCode >= 400) {
            errorCounter.increment();
            meterRegistry.counter("bff.requests.errors", 
                    "endpoint", endpoint, 
                    "status", String.valueOf(statusCode))
                    .increment();
        }

        meterRegistry.gauge("bff.response.status", statusCode);
    }

    /**
     * Record downstream service call
     */
    public void recordDownstreamCall(String service, long duration, boolean success) {
        downstreamLatency.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        meterRegistry.counter("bff.downstream.calls",
                "service", service,
                "status", success ? "success" : "failed")
                .increment();
    }

    /**
     * Record cache operations
     */
    public void recordCacheHit(String cacheKey) {
        cacheHitCounter.increment();
        meterRegistry.counter("bff.cache.operations",
                "type", "hit",
                "key", cacheKey)
                .increment();
    }

    public void recordCacheMiss(String cacheKey) {
        cacheMissCounter.increment();
        meterRegistry.counter("bff.cache.operations",
                "type", "miss",
                "key", cacheKey)
                .increment();
    }

    /**
     * Get cache hit ratio
     */
    public double getCacheHitRatio() {
        double hits = cacheHitCounter.count();
        double misses = cacheMissCounter.count();
        double total = hits + misses;
        return total > 0 ? (hits / total) * 100 : 0;
    }
}
