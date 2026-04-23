package com.yas.payment.paypal.viewmodel;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaypalCapturePaymentResponse(
        String checkoutId,
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        String paymentMethod,
        String paymentStatus,
        String failureMessage) {
}