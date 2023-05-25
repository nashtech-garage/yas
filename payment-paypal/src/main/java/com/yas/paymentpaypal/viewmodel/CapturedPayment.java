package com.yas.paymentpaypal.viewmodel;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CapturedPayment(
        Long orderId,
        String checkoutId,
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        String paymentMethod,
        String paymentStatus,
        String failureMessage ) {

}