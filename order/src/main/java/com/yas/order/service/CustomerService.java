package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.CustomerVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class CustomerService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public CustomerService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public CustomerVm getCustomer() {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.customer())
                .path("/storefront/customer/profile")
                .buildAndExpand()
                .toUri();
        return webClient.get()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .retrieve()
                .bodyToMono(CustomerVm.class)
                .block();
    }
}
