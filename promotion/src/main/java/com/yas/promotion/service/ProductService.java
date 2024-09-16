package com.yas.promotion.service;

import com.yas.promotion.config.ServiceUrlConfig;
import com.yas.promotion.utils.AuthenticationUtils;
import com.yas.promotion.viewmodel.BrandVm;
import com.yas.promotion.viewmodel.CategoryGetVm;
import com.yas.promotion.viewmodel.ProductVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleFallback")
    public List<ProductVm> getProductByIds(List<Long> ids) {
        String jwt = AuthenticationUtils.extractJwt();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/products/by-ids")
            .queryParams(createIdParams(ids))
            .build()
            .toUri();

        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ProductVm>>() {
            })
            .getBody();
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleFallback")
    public List<CategoryGetVm> getCategoryByIds(List<Long> ids) {
        String jwt = AuthenticationUtils.extractJwt();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/categories/by-ids")
            .queryParams(createIdParams(ids))
            .build()
            .toUri();
        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<CategoryGetVm>>() {
            })
            .getBody();
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleFallback")
    public List<BrandVm> getBrandByIds(List<Long> ids) {
        String jwt = AuthenticationUtils.extractJwt();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/brands/by-ids")
            .queryParams(createIdParams(ids))
            .build()
            .toUri();
        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<BrandVm>>() {
            })
            .getBody();
    }

    private static MultiValueMap<String, String> createIdParams(List<Long> ids) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        ids.stream().map(Objects::toString).forEach(id -> params.add("ids", id));
        return params;
    }
}
