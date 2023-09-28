package com.yas.saga.order.reply;

import lombok.Builder;

@Builder
public record UpdateOrderPaymentStatusSuccess(Long orderId, String orderStatus) {
}
