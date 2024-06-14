package com.yas.payment.viewmodel;

import lombok.Builder;

@Builder
public record PaymentOrderStatusVm(
        Long orderId,
        String orderStatus,
        Long paymentId,
        String paymentStatus
) {
}
