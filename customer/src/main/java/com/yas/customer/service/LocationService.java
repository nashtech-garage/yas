package com.yas.customer.service;

import com.yas.customer.config.ServiceUrlConfig;
import com.yas.customer.viewmodel.Address.AddressGetVm;
import com.yas.customer.viewmodel.Address.AddressPostVm;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class LocationService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public LocationService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public List<AddressGetVm> getAddressesByIdList(List<Long> ids) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/addresses")
                .queryParam("ids", ids)
                .buildAndExpand()
                .toUri();
        return webClient.get()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AddressGetVm>>() {})
                .block();
    }

    public AddressGetVm createAddress(AddressPostVm addressPostVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/address")
                .buildAndExpand()
                .toUri();

        return webClient.post()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .bodyValue(addressPostVm)
                .retrieve()
                .bodyToMono(AddressGetVm.class)
                .block();
    }
}
