package com.yas.cart.viewmodel;

import java.util.List;

import com.yas.cart.model.Cart;

public record CartGetDetailVm(Long id, String customerId, Long orderId, List<CartDetailVm> cartDetails) {
    public static CartGetDetailVm fromModel(Cart cart) {
        return new CartGetDetailVm(
                cart.getId(),
                cart.getCustomerId(),
                cart.getOrderId(),
                cart.getCartItems().stream().map(CartDetailVm::fromModel).toList());
    }
}
