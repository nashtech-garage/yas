package com.yas.cart.viewmodel;

import java.util.List;

import com.yas.cart.model.Cart;

public record CartGetDetailVm(Long id, String customerId, List<CartDetailVm> cartDetails) {
    public static CartGetDetailVm fromModel(Cart cart) {
        return new CartGetDetailVm(
                cart.getId(),
                cart.getCustomerId(),
                cart.getCartItems().stream().map(CartDetailVm::fromModel).toList());
    }
}
