package com.yas.cart.viewmodel;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public record CartPostVM(
        @NotEmpty Long id,
        @NotEmpty String customerId,
        List<CartDetailListVM> cartDetailListVMs) {
}
