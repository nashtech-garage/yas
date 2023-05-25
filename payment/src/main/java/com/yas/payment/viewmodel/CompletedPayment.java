package com.yas.payment.viewmodel;

import com.yas.payment.model.enumeration.EPaymentMethod;
import com.yas.payment.model.enumeration.EPaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

public record CompletedPayment(
        Long orderId,
        String checkoutId,
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        EPaymentMethod paymentMethod,
        EPaymentStatus paymentStatus,
        String failureMessage ) {

}