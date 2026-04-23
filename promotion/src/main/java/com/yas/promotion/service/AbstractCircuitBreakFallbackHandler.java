package com.yas.promotion.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class AbstractCircuitBreakFallbackHandler {

    protected void handleBodilessFallback(List<Long> ids, Throwable throwable) throws Throwable {
        handleError(ids, throwable);
    }

    protected Object handleFallback(List<Long> ids, Throwable throwable) throws Throwable {
        handleError(ids, throwable);
        return null;
    }


    private void handleError(List<Long> ids, Throwable throwable) throws Throwable {
        log.error("Circuit breaker records an error. Detail {} with ids {}", throwable.getMessage(), ids);
        throw throwable;
    }
}
