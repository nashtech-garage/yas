package com.yas.cart.viewmodel;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public record CartPostVm(
        List<CartItemVm> cartItemVm) {
}
