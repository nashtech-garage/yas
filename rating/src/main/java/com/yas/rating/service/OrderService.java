package com.yas.rating.service;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.OrderExistsByProductAndUserGetVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OrderService extends AbstractCircuitBreakFallbackHandler {

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleFallback")
    public OrderExistsByProductAndUserGetVm checkOrderExistsByProductAndUserWithStatus(final Long productId) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.order())
                .path("/storefront/orders/completed")
                .queryParam("productId", productId.toString())
                .buildAndExpand()
                .toUri();
        return restClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .body(OrderExistsByProductAndUserGetVm.class);
    }

    public OrderExistsByProductAndUserGetVm handleFallback(@SuppressWarnings("unused") Long productId, @SuppressWarnings("unused") Throwable t) {
        return new OrderExistsByProductAndUserGetVm(false);
    }
}
