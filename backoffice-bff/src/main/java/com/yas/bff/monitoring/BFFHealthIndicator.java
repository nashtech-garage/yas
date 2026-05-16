package com.yas.bff.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom health indicator for monitoring downstream services
 */
@Component("bff-health")
public class BFFHealthIndicator implements HealthIndicator {

    private final WebClient webClient;
    private static final long HEALTH_CHECK_TIMEOUT = 5000; // 5 seconds

    public BFFHealthIndicator(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", LocalDateTime.now());
        details.put("version", "1.0");

        // Check downstream services health
        Map<String, ServiceHealth> servicesStatus = checkDownstreamServices();
        details.put("services", servicesStatus);

        // Determine overall status
        long downServices = servicesStatus.values().stream()
                .filter(s -> !s.isHealthy())
                .count();

        if (downServices == 0) {
            details.put("description", "All services operational");
            return Health.up()
                    .withDetails(details)
                    .build();
        } else if (downServices < servicesStatus.size() / 2) {
            details.put("description", downServices + " service(s) degraded");
            return Health.degraded()
                    .withDetails(details)
                    .build();
        } else {
            details.put("description", "Multiple services down");
            return Health.down()
                    .withDetails(details)
                    .build();
        }
    }

    /**
     * Check health of key downstream services
     */
    private Map<String, ServiceHealth> checkDownstreamServices() {
        Map<String, ServiceHealth> services = new HashMap<>();

        String[] serviceEndpoints = {
                "http://product:8092/actuator/health",
                "http://customer:8093/actuator/health",
                "http://cart:8094/actuator/health",
                "http://order:8095/actuator/health"
        };

        for (String endpoint : serviceEndpoints) {
            String serviceName = extractServiceName(endpoint);
            try {
                webClient.get()
                        .uri(endpoint)
                        .retrieve()
                        .toEntity(String.class)
                        .timeout(java.time.Duration.ofMillis(HEALTH_CHECK_TIMEOUT))
                        .subscribe(
                                response -> services.put(serviceName,
                                        ServiceHealth.builder()
                                                .name(serviceName)
                                                .healthy(response.getStatusCode().is2xxSuccessful())
                                                .responseTime(LocalDateTime.now())
                                                .build()),
                                error -> services.put(serviceName,
                                        ServiceHealth.builder()
                                                .name(serviceName)
                                                .healthy(false)
                                                .responseTime(LocalDateTime.now())
                                                .error(error.getMessage())
                                                .build())
                        );
            } catch (Exception e) {
                services.put(serviceName,
                        ServiceHealth.builder()
                                .name(serviceName)
                                .healthy(false)
                                .error(e.getMessage())
                                .responseTime(LocalDateTime.now())
                                .build());
            }
        }

        return services;
    }

    private String extractServiceName(String endpoint) {
        return endpoint.split("://")[1].split(":")[0];
    }

    /**
     * Service health status DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceHealth {
        private String name;
        private boolean healthy;
        private LocalDateTime responseTime;
        private String error;
    }
}
