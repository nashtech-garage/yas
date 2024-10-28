package com.yas.payment.viewmodel;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CapturePaymentResponse(
        Long orderId,
        String checkoutId,
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String failureMessage) {
}