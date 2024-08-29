package com.yas.cart.viewmodel;

import com.yas.cart.model.CartItem;

public record CartDetailVm(Long id, Long productId, int quantity) {
    public static CartDetailVm fromModel(CartItem cartItem) {
        return new CartDetailVm(
            cartItem.getId(),
            cartItem.getProductId(),
            cartItem.getQuantity());
    }
}
