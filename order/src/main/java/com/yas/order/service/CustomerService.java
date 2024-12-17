package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.customer.ActiveAddressVm;
import com.yas.order.viewmodel.customer.CustomerVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CustomerService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleCustomerFallback")
    public CustomerVm getCustomer() {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.customer())
                .path("/storefront/customer/profile")
                .buildAndExpand()
                .toUri();
        return restClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .body(CustomerVm.class);
    }

    protected CustomerVm handleCustomerFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleCustomerListFallback")
    public List<ActiveAddressVm> getUserAddresses() {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.customer())
            .path("/storefront/user-address")
            .buildAndExpand()
            .toUri();
        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ActiveAddressVm>>() {
            })
            .getBody();
    }

    protected List<ActiveAddressVm> handleCustomerListFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
