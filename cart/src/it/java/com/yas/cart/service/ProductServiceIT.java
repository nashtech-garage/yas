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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.context.annotation.Import;

import com.yas.commonlibrary.IntegrationTestConfiguration;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
class ProductServiceIT {
    @MockitoSpyBean
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
    // Additional test cases can be added here to cover other scenarios, such as successful retrieval of products, handling of exceptions, etc.
    // Additional test cases can be added here to cover other scenarios, such as successful retrieval of products, handling of exceptions, etc.
    // Additional test cases can be added here to cover other scenarios, such as successful retrieval of products, handling of exceptions, etc.
    // Additional test cases can be added here to cover other scenarios, such as successful retrieval of products, handling of exceptions, etc.
    // Additional test cases can be added here to cover other scenarios, such as successful retrieval of products, handling of exceptions, etc.
}
