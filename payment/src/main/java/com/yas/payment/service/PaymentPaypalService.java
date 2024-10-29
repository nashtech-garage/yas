package com.yas.payment.service;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.viewmodel.PaymentPaypalRequest;
import com.yas.payment.viewmodel.PaymentPaypalResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentPaypalService extends AbstractCircuitBreakFallbackHandler {

    private final RestClient restClient;

    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handlePaymentPaypalResponseFallback")
    public PaymentPaypalResponse createOrderOnPaypal(PaymentPaypalRequest requestPayment) {

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.paymentPaypal())
            .path("/events/checkout/orders")
            .build()
            .toUri();

        return restClient.post()
            .uri(url)
            .body(requestPayment)
            .retrieve()
            .body(PaymentPaypalResponse.class);
    }

    private PaymentPaypalResponse handlePaymentPaypalResponseFallback(Throwable throwable)
        throws Throwable {
        return handleTypedFallback(throwable);
    }
}
