package com.yas.paymentpaypal.viewmodel;

import java.math.BigDecimal;

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