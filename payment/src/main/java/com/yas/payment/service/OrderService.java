package com.yas.payment.service;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.viewmodel.CapturePaymentResponse;
import com.yas.payment.viewmodel.CheckoutStatusVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public Long updateCheckoutStatus(CapturePaymentResponse capturePaymentResponse) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.order())
                .path("/storefront/checkouts/status")
                .buildAndExpand()
                .toUri();
        CheckoutStatusVm checkoutStatusVm = new CheckoutStatusVm(capturePaymentResponse.checkoutId(),
            capturePaymentResponse.paymentStatus().name());

        return restClient.put()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(checkoutStatusVm)
            .retrieve()
            .body(Long.class);
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handlePaymentOrderStatusFallback")
    public PaymentOrderStatusVm updateOrderStatus(PaymentOrderStatusVm orderPaymentStatusVm) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.order())
            .path("/storefront/orders/status")
            .buildAndExpand()
            .toUri();

        return restClient.put()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
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
