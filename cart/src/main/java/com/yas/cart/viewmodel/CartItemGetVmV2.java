package com.yas.cart.viewmodel;

import lombok.Builder;

@Builder
public record CartItemGetVmV2(
    String customerId,
    Long productId,
    Integer quantity
) {}
