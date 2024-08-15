package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductQuantityItem;
import com.yas.order.viewmodel.product.ProductVariationVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleFallback")
    public List<ProductVariationVm> getProductVariations(Long productId) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/backoffice/product-variations/" + productId)
                .buildAndExpand()
                .toUri();

        return restClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductVariationVm>>(){})
                .getBody();
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBodilessFallback")
    public void subtractProductStockQuantity(OrderVm orderVm) {

        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/backoffice/products/subtract-quantity")
                .buildAndExpand()
                .toUri();

        restClient.put()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(buildProductQuantityItems(orderVm.orderItemVms()))
                .retrieve();
    }

    private List<ProductQuantityItem> buildProductQuantityItems(Set<OrderItemVm> orderItems) {
        return orderItems.stream()
                .map(orderItem ->
                        ProductQuantityItem
                                .builder()
                                .productId(orderItem.productId())
                                .quantity(Long.valueOf(orderItem.quantity()))
                                .build()
                ).toList();
    }
}
