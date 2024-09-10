package com.yas.inventory.service;


import static com.yas.inventory.constant.TestConstants.CIRCUIT_BREAKER_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
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
class ProductServiceITTemp {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16");
    @SpyBean
    private ProductService productService;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void test_getProduct_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() throws Throwable {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
        assertThrows(CallNotPermittedException.class, () -> productService.getProduct(1L));
        verify(productService, atLeastOnce()).handleProductInfoFallback(any());
    }

    @Test
    void test_filterProducts_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() throws Throwable {
        String productName = "product name";
        String productSku = "product sku";
        List<Long> productIds = List.of(1L);
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
        assertThrows(CallNotPermittedException.class,
            () -> productService.filterProducts(productName, productSku, productIds, FilterExistInWhSelection.ALL));
        verify(productService, atLeastOnce()).handleProductInfoListFallback(any());
    }
}