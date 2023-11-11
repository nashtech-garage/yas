package com.yas.payment.saga.data;

import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.EPaymentStatus;
import lombok.Builder;

@Builder
public record PaymentData(Long paymentId, EPaymentStatus paymentStatus) {
    public static PaymentData from(Payment payment) {
        return PaymentData
                .builder()
                .paymentId(payment.getId())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }
}
