package com.yas.cart.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void handleBodilessFallback_shouldRethrowThrowable() {
        RuntimeException throwable = new RuntimeException("fallback failed");

        assertThatThrownBy(() -> handler.handleBodilessFallback(throwable))
            .isSameAs(throwable);
    }

    @Test
    void handleTypedFallback_shouldRethrowThrowable() {
        RuntimeException throwable = new RuntimeException("typed fallback failed");

        assertThatThrownBy(() -> handler.handleTypedFallback(throwable))
            .isSameAs(throwable);
    }

    private static final class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
    }
}
