package com.yas.paymentpaypal.viewmodel;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CapturedPaymentVm(
        Long orderId,
        String checkoutId,
        BigDecimal amount,
        BigDecimal paymentFee,
        String gatewayTransactionId,
        String paymentMethod,
        String paymentStatus,
        String failureMessage) {

}