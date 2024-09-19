package com.yas.cart.service;

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
import org.springframework.context.annotation.ComponentScan;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ComponentScan(basePackages = {"com.yas.commonlibrary"})
class ProductServiceIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16");
    @SpyBean
    private ProductService productService;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void test_getProducts_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() throws Throwable {
        List<Long> productIds = List.of(1L);
        circuitBreakerRegistry.circuitBreaker("restCircuitBreaker").transitionToOpenState();
        assertThrows(CallNotPermittedException.class, () -> productService.getProducts(productIds));
        verify(productService, atLeastOnce()).handleProductThumbnailFallback(any());
    }
}
