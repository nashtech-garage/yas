package com.yas.cart.service;

import com.yas.cart.config.ServiceUrlConfig;
import com.yas.cart.viewmodel.ProductThumbnailVm;
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
public class ProductService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleProductThumbnailFallback")
    public List<ProductThumbnailVm> getProducts(List<Long> ids) {
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/storefront/products/list-featured")
            .queryParam("productId", ids)
            .build()
            .toUri();
        return restClient.get()
            .uri(url)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
            })
            .getBody();
    }

    protected List<ProductThumbnailVm> handleProductThumbnailFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
