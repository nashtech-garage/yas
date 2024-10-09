package com.yas.cart.viewmodel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CartItemV2PostVm(
    @NotNull Long productId,
    @NotNull @Min(1) Integer quantity) {}
