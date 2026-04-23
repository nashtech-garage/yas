package com.yas.payment.viewmodel;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CapturePaymentResponseVm(
        Long orderId,
        String checkoutId,
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String failureMessage) {
}