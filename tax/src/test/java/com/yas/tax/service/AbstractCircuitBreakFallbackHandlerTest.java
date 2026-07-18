package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    @Test
    void handleTypedFallbackShouldRethrowOriginalThrowable() {
        TestFallbackHandler handler = new TestFallbackHandler();
        IllegalStateException original = new IllegalStateException("location unavailable");

        assertThatThrownBy(() -> handler.invoke(original))
            .isSameAs(original);
    }

    @Test
    void handleBodilessFallbackShouldRethrowOriginalThrowable() {
        TestFallbackHandler handler = new TestFallbackHandler();
        IllegalStateException original = new IllegalStateException("location unavailable");

        assertThatThrownBy(() -> handler.invokeBodiless(original))
            .isSameAs(original);
    }

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        Object invoke(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }

        void invokeBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }
    }
}
