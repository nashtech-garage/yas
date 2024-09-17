package com.yas.product.service;

import static com.yas.product.constant.TestConstants.CIRCUIT_BREAKER_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.product.config.IntegrationTestConfiguration;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
class MediaServiceIT {
    @Autowired
    private MediaService mediaService;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void test_getMedia_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
        assertThrows(CallNotPermittedException.class, () -> mediaService.getMedia(1L));
    }
}
