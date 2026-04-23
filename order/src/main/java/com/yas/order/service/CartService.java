package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.cart.CartItemDeleteVm;
import com.yas.order.viewmodel.order.OrderVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CartService extends AbstractCircuitBreakFallbackHandler {

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBodilessFallback")
    public void deleteCartItems(OrderVm orderVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();

        List<CartItemDeleteVm> cartItemDeleteVms = orderVm.orderItemVms()
            .stream()
            .map(orderItemVm -> new CartItemDeleteVm(orderItemVm.productId(), orderItemVm.quantity()))
            .toList();

        final URI url = UriComponentsBuilder
            .fromUriString(serviceUrlConfig.cart())
            .path("/storefront/cart/items/remove")
            .buildAndExpand()
            .toUri();

        restClient.post()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .body(cartItemDeleteVms)
            .retrieve();
    }
}
