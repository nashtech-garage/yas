package com.yas.inventory.service;

import com.yas.inventory.config.ServiceUrlConfig;
import com.yas.inventory.exception.AccessDeniedException;
import com.yas.inventory.exception.NotFoundException;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class LocationService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public LocationService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public AddressDetailVm getAddressById(Long id) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/addresses/{id}")
                .buildAndExpand(id)
                .toUri();

        return webClient.get()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .bodyToMono(AddressDetailVm.class)
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

    public Void updateAddress(Long id, AddressPostVm addressPostVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/addresses/{id}")
                .buildAndExpand(id)
                .toUri();

        return webClient.put()
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
                .bodyToMono(void.class)
                .block();
    }

    public void deleteAddress(Long addressId){
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.location()).path("/storefront/addresses/{id}").buildAndExpand(addressId).toUri();
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        try{
            webClient.delete()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwt))
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
                    .bodyToMono(Void.class)
                    .block();
        }
        catch (WebClientResponseException e){
            throw new NotFoundException(e.getMessage());
        }
    }
}