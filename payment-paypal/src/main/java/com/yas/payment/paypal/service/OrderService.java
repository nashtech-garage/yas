package com.yas.payment.paypal.service;

import com.yas.paymentpaypal.config.ServiceUrlConfig;
import com.yas.payment.paypal.viewmodel.OrderVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBodilessFallback")
    public OrderVm getOrderByCheckoutId(String checkoutId) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.order())
            .path("/storefront/orders/checkout/" + checkoutId)
            .buildAndExpand()
            .toUri();

        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(OrderVm.class);
    }
}
