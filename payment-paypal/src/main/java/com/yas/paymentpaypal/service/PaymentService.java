package com.yas.paymentpaypal.service;

import com.yas.paymentpaypal.config.ServiceUrlConfig;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService extends AbstractCircuitBreakFallbackHandler {

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBodilessFallback")
    public void capturePayment(CapturedPaymentVm completedPayment) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.payment())
                .path("/storefront/payments/capture")
                .buildAndExpand()
                .toUri();

        restClient.post()
                .uri(url)
                .body(completedPayment)
                .retrieve();
    }
}
