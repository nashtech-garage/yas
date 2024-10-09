package com.yas.cart.viewmodel;

import lombok.Builder;

@Builder
public record CartItemV2GetVm(
    String customerId,
    Long productId,
    Integer quantity
) {}
