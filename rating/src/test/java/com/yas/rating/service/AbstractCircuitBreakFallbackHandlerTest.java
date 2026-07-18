package com.yas.rating.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void handleFallback_whenCalled_thenRethrowsOriginalError() {
        RuntimeException error = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.handleFallback(error));

        assertSame(error, thrown);
    }

    @Test
    void handleBodilessFallback_whenCalled_thenRethrowsOriginalError() {
        RuntimeException error = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(error));

        assertSame(error, thrown);
    }

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
    }
}
