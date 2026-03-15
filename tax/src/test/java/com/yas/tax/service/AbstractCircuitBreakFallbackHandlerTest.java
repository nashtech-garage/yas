package com.yas.tax.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    // Tạo một instance nặc danh (anonymous class) để test class trừu tượng
    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleBodilessFallback_ShouldThrowException() {
        Throwable exception = new RuntimeException("Service Unavailable");

        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(exception));
    }

    @Test
    void handleTypedFallback_ShouldThrowException() {
        Throwable exception = new RuntimeException("Timeout error");

        assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(exception));
    }
}