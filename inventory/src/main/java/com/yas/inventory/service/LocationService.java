package com.yas.inventory.service;

import com.yas.inventory.config.ServiceUrlConfig;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LocationService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retryable(maxAttemptsExpression = "${spring.retry.maxAttempts}")
    @CircuitBreaker(name = "cart-circuitbreaker", fallbackMethod = "handleFallback")
    public AddressDetailVm getAddressById(Long id) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses/{id}")
            .buildAndExpand(id)
            .toUri();

        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(AddressDetailVm.class);
    }

    @Retryable(maxAttemptsExpression = "${spring.retry.maxAttempts}")
    @CircuitBreaker(name = "inventory-circuitbreaker", fallbackMethod = "handleFallback")
    public AddressVm createAddress(AddressPostVm addressPostVm) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses")
            .buildAndExpand()
            .toUri();

        return restClient.post()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .body(addressPostVm)
            .retrieve()
            .body(AddressVm.class);
    }

    @Retryable(maxAttemptsExpression = "${spring.retry.maxAttempts}")
    @CircuitBreaker(name = "inventory-circuitbreaker", fallbackMethod = "handleBodilessFallback")
    public void updateAddress(Long id, AddressPostVm addressPostVm) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();

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

    @Retryable(maxAttemptsExpression = "${spring.retry.maxAttempts}")
    @CircuitBreaker(name = "inventory-circuitbreaker", fallbackMethod = "handleBodilessFallback")
    public void deleteAddress(Long addressId) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.location()).path("/storefront/addresses/{id}")
            .buildAndExpand(addressId).toUri();
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        restClient.delete()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(Void.class);
    }
}