package com.yas.order.service;

import static com.yas.order.constant.TestConstants.CIRCUIT_BREAKER_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CustomerServiceIT {
    @Autowired
    private CustomerService locationService;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    void test_getCustomer_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
        assertThrows(CallNotPermittedException.class, () -> locationService.getCustomer());
    }
}
