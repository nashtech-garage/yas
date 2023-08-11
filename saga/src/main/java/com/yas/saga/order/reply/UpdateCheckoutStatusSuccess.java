package com.yas.saga.order.reply;

import lombok.Builder;

@Builder
public record UpdateCheckoutStatusSuccess(Long orderId) {}
