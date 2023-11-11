package com.yas.payment.viewmodel;

import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.EPaymentMethod;
import com.yas.payment.model.enumeration.EPaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;

@Builder
public record CapturedPayment(
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        EPaymentMethod paymentMethod,
        EPaymentStatus paymentStatus,
        String failureMessage ) {
    public static CapturedPayment fromModel(Payment payment){
        return CapturedPayment.builder()
                .amount(payment.getAmount())
                .paymentFee(payment.getPaymentFee())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .failureMessage(payment.getFailureMessage())
                .build();
    }

}