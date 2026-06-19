package com.yas.payment.paypal.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleBodilessFallback_shouldThrowException() {
        Throwable throwable = new RuntimeException("test error");
        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(throwable));
    }

    @Test
    void handleTypedFallback_shouldThrowException() {
        Throwable throwable = new RuntimeException("test error");
        assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(throwable));
    }

    @Test
    void handleError_shouldLogErrorAndThrow() {
        Throwable throwable = new Exception("checked exception");
        Exception exception = assertThrows(Exception.class, () -> handler.handleBodilessFallback(throwable));
        assertThat(exception.getMessage()).isEqualTo("checked exception");
    }
}
