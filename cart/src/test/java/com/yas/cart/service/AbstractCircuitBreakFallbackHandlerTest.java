package com.yas.cart.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestFallbackHandler fallbackHandler;

    @BeforeEach
    void setUp() {
        fallbackHandler = new TestFallbackHandler();
    }

    @Test
    void handleBodilessFallback_shouldRethrowOriginalThrowable() {
        IllegalStateException error = new IllegalStateException("fallback failed");

        assertThatThrownBy(() -> fallbackHandler.handleBodilessFallback(error))
            .isSameAs(error);
    }

    @Test
    void handleTypedFallback_shouldRethrowOriginalThrowable() {
        IllegalArgumentException error = new IllegalArgumentException("typed fallback failed");

        assertThatThrownBy(() -> fallbackHandler.handleTypedFallback(error))
            .isSameAs(error);
    }

    private static final class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
    }
}
