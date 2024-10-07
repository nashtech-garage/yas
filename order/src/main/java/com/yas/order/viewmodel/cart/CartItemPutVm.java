package com.yas.order.viewmodel.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CartItemPutVm(@NotNull @Min(1) Integer quantity) {}
