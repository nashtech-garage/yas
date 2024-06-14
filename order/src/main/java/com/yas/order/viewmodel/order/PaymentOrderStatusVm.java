package com.yas.order.viewmodel.order;

import lombok.Builder;

@Builder
public record PaymentOrderStatusVm(
        Long orderId,
        String orderStatus,
        Long paymentId,
        String paymentStatus
) {
}
