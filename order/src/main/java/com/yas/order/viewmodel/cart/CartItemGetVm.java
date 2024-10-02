package com.yas.order.viewmodel.cart;

import lombok.Builder;

@Builder
public record CartItemGetVm(
        String customerId,
        Long productId,
        Integer quantity
) {}
