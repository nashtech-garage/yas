package com.yas.inventory.service;

import com.yas.inventory.config.ServiceUrlConfig;
import com.yas.inventory.exception.AccessDeniedException;
import com.yas.inventory.exception.NotFoundException;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public AddressDetailVm getAddressById(Long id) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/addresses/{id}")
                .buildAndExpand(id)
                .toUri();

        return restClient.get()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .retrieve()
                .onStatus(
                    HttpStatus.NOT_FOUND::equals,
                    (request, response) -> {
                        String body = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);
                        throw new NotFoundException(body);
                    })
                .body(AddressDetailVm.class);
    }

    public AddressVm createAddress(AddressPostVm addressPostVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/addresses")
                .buildAndExpand()
                .toUri();

        return restClient.post()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .body(addressPostVm)
                .retrieve()
                .body(AddressVm.class);
    }

    public void updateAddress(Long id, AddressPostVm addressPostVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.location())
                .path("/storefront/addresses/{id}")
                .buildAndExpand(id)
                .toUri();

        restClient.put()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .body(addressPostVm)
            .retrieve()
            .body(Void.class);
    }

    public void deleteAddress(Long addressId){
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.location()).path("/storefront/addresses/{id}").buildAndExpand(addressId).toUri();
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        restClient.delete()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(Void.class);
    }
}