package com.yas.rating.service;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.OrderExistsByProductAndUserGetVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class OrderService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public OrderService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public OrderExistsByProductAndUserGetVm checkOrderExistsByProductAndUserWithStatus(final String status,
                                                           final Long productId) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.order())
                .path("/storefront/orders")
                .queryParam("status", status)
                .queryParam("productId", productId.toString())
                .buildAndExpand()
                .toUri();

        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .bodyToMono(OrderExistsByProductAndUserGetVm.class)
                .block();
    }
}
