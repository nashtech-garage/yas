package com.yas.cart.viewmodel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemV2PutVm(@NotNull @Min(1) Integer quantity) {}