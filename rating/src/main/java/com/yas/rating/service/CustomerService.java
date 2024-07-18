package com.yas.rating.service;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.CustomerVm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public CustomerVm getCustomer() {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.customer())
                .path("/storefront/customer/profile")
                .buildAndExpand()
                .toUri();
        return restClient.get()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .retrieve()
                .body(CustomerVm.class);
    }
}
