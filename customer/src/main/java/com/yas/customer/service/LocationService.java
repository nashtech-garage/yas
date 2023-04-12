package com.yas.customer.service;


import com.yas.customer.config.ServiceUrlConfig;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import com.yas.customer.exception.NotFoundException;
import com.yas.customer.exception.AccessDeniedException;

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

    public List<AddressDetailVm> getAddressesByIdList(List<Long> ids) {
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
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
                .bodyToMono(new ParameterizedTypeReference<List<AddressDetailVm>>() {})
                .block();
    }

    public AddressVm createAddress(AddressPostVm addressPostVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/addresses")
                .buildAndExpand()
                .toUri();

        return webClient.post()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .bodyValue(addressPostVm)
                .retrieve()
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
                .onStatus(
                        HttpStatus.FORBIDDEN::equals,
                        response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .bodyToMono(AddressVm.class)
                .block();
    }
}
