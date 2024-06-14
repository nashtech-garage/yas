package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductQuantityItem;
import com.yas.order.viewmodel.product.ProductVariationVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final WebClient webClient;
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public List<ProductVariationVM> getProductVariations(Long productId) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/backoffice/product-variations/" + productId)
                .buildAndExpand()
                .toUri();

        return webClient.get()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .retrieve()
                .bodyToFlux(ProductVariationVM.class)
                .collectList().block();
    }

    public void subtractProductStockQuantity(OrderVm orderVm) {

        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
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
