package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.payment.CheckoutPaymentVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentService extends AbstractCircuitBreakFallbackHandler {

    private final RestClient restClient;

    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleLongFallback")
    public Long createPaymentFromEvent(CheckoutPaymentVm checkoutPaymentRequestDto) {

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.payment())
            .path("/events/payments")
            .buildAndExpand()
            .toUri();

        return restClient.post()
            .uri(url)
            .body(checkoutPaymentRequestDto)
            .retrieve()
            .body(Long.class);
    }

    private Long handleLongFallback(Throwable throwable)
        throws Throwable {
        return handleTypedFallback(throwable);
    }
}
