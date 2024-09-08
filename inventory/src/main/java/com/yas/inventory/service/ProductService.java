package com.yas.inventory.service;

import com.yas.inventory.config.ServiceUrlConfig;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.utils.AuthenticationUtils;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleProductInfoFallback")
    public ProductInfoVm getProduct(Long id) {
        String jwt = AuthenticationUtils.extractJwt();

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/products/" + id)
            .build()
            .toUri();
        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(ProductInfoVm.class);
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleProductInfoListFallback")
    public List<ProductInfoVm> filterProducts(String productName, String productSku,
                                              List<Long> productIds, FilterExistInWhSelection selection) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", productName);
        params.add("sku", productSku);
        params.add("selection", selection.name());
        if (!CollectionUtils.isEmpty(productIds)) {
            params.add("productIds", productIds.stream().map(Object::toString).collect(Collectors.joining(",")));
        }
        String jwt = AuthenticationUtils.extractJwt();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/products/for-warehouse")
            .queryParams(params)
            .build()
            .toUri();
        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ProductInfoVm>>() {
            })
            .getBody();
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBodilessFallback")
    public void updateProductQuantity(List<ProductQuantityPostVm> productQuantityPostVms) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/products/update-quantity")
            .buildAndExpand()
            .toUri();

        restClient.put()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .body(productQuantityPostVms)
            .retrieve()
            .body(Void.class);
    }

    protected ProductInfoVm handleProductInfoFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }

    protected List<ProductInfoVm> handleProductInfoListFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
