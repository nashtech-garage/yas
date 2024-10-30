package com.yas.product.service;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class InventoryService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleInventoryFallback")
    public List<Long> getProductIdsAddedWarehouse() {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.inventory())
            .path("/storefront/stocks/products-in-warehouse").buildAndExpand().toUri();
        return restClient.get()
            .uri(url)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });
    }

    private List<Long> handleInventoryFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
