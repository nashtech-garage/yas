package com.yas.payment.viewmodel;

import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CapturedPayment(
        Long orderId,
        String checkoutId,
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String failureMessage) {
    public static CapturedPayment fromModel(Payment payment) {
        return CapturedPayment.builder()
                .amount(payment.getAmount())
                .paymentFee(payment.getPaymentFee())
                .checkoutId(payment.getCheckoutId())
                .orderId(payment.getOrderId())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .failureMessage(payment.getFailureMessage())
                .build();
    }

}