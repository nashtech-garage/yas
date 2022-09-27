package com.yas.cart.viewmodel;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public record CartPostVm(
        @NotEmpty String customerId,
        List<CartItemVm> cartItemVm) {
}
