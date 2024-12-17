package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.payment.PaymentProviderVm;
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
public class PaymentService extends AbstractCircuitBreakFallbackHandler{
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleListFallback")
    public List<PaymentProviderVm> getPaymentProviders() {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.payment())
            .path("/storefront/payment-providers")
            .buildAndExpand()
            .toUri();

        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<PaymentProviderVm>>() {
            })
            .getBody();
    }

    protected List<PaymentProviderVm> handleListFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
