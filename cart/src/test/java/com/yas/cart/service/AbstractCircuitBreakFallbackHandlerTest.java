package com.yas.cart.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        void callBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        Object callTyped(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void handleBodilessFallback_whenErrorPassed_thenRethrowSameThrowable() {
        RuntimeException exception = new RuntimeException("bodiless-error");

        assertThatThrownBy(() -> handler.callBodiless(exception))
            .isSameAs(exception);
    }

    @Test
    void handleTypedFallback_whenErrorPassed_thenRethrowSameThrowable() {
        IllegalStateException exception = new IllegalStateException("typed-error");

        assertThatThrownBy(() -> handler.callTyped(exception))
            .isSameAs(exception);
    }
}
