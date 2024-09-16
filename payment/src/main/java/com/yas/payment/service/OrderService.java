package com.yas.payment.service;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.CheckoutStatusVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
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
public class OrderService extends AbstractCircuitBreakFallbackHandler {

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleLongFallback")
    public Long updateCheckoutStatus(CapturedPayment capturedPayment) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.order())
                .path("/storefront/checkouts/status")
                .buildAndExpand()
                .toUri();
        CheckoutStatusVm checkoutStatusVm = new CheckoutStatusVm(capturedPayment.checkoutId(),
            capturedPayment.paymentStatus().name());

        return restClient.put()
                .uri(url)
                .body(checkoutStatusVm)
            .retrieve()
            .body(Long.class);
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handlePaymentOrderStatusFallback")
    public PaymentOrderStatusVm updateOrderStatus(PaymentOrderStatusVm orderPaymentStatusVm) {

        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.order())
                .path("/storefront/orders/status")
                .buildAndExpand()
                .toUri();

        return restClient.put()
                .uri(url)
                .body(orderPaymentStatusVm)
                .retrieve()
                .body(PaymentOrderStatusVm.class);
    }

    protected Long handleLongFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }

    protected PaymentOrderStatusVm handlePaymentOrderStatusFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
