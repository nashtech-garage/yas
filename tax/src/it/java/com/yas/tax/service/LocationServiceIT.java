package com.yas.tax.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class LocationServiceIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16");
    @SpyBean
    private LocationService locationService;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void test_getStateOrProvinceAndCountryNames_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen()
        throws Throwable {
        circuitBreakerRegistry.circuitBreaker("restCircuitBreaker").transitionToOpenState();
        List<Long> stateOrProvinceIds = List.of(1L);
        assertThrows(CallNotPermittedException.class,
            () -> locationService.getStateOrProvinceAndCountryNames(stateOrProvinceIds));
        verify(locationService, atLeastOnce()).handleLocationNameListFallback(any());
    }
}