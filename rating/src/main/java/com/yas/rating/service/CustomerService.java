package com.yas.rating.service;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.exception.NotFoundException;
import com.yas.rating.viewmodel.CustomerVm;
import com.yas.rating.viewmodel.ProductThumbnailVm;
import org.springframework.http.HttpStatus;
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
