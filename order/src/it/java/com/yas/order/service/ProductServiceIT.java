package com.yas.order.service;

import static com.yas.order.constant.TestConstants.CIRCUIT_BREAKER_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
class ProductServiceIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16");
    @SpyBean
    private ProductService productService;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Nested
    class CircuitFallbackTest {

        @AfterEach
        void tearDown() {
            circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToClosedState();
        }

        @Test
        void test_getProductVariations_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() throws Throwable {
            circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
            assertThrows(CallNotPermittedException.class, () -> productService.getProductVariations(1L));
            verify(productService, atLeastOnce()).handleProductVariationListFallback(any());
        }

        @Test
        void test_getProducts_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() throws Throwable {
            List<Long> productsIds = List.of(1L);
            circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
            assertThrows(CallNotPermittedException.class, () -> productService.getProducts(productsIds));
            verify(productService, atLeastOnce()).handleProductThumbnailListFallback(any());
        }
    }
}
