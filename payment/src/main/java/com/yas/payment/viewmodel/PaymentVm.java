package com.yas.payment.viewmodel;

import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentVm(
        @NotNull Long id,
        @NotNull Long orderId,
        @NotNull String checkoutId,
        @NotNull BigDecimal amount,
        BigDecimal paymentFee,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String gatewayTransactionId,
        String failureMessage
) {

    public static PaymentVm fromModel(Payment payment) {
        return new PaymentVm(
                payment.getId(),
                payment.getOrderId(),
                payment.getCheckoutId(),
                payment.getAmount(),
                payment.getPaymentFee(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getGatewayTransactionId(),
                payment.getFailureMessage()
        );
    }
}
