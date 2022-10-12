package com.yas.cart.viewmodel;

import com.yas.cart.model.CartItem;

public record CartItemVm(Long productId, int quantity) {
    public static CartItemVm fromModel(CartItem cartItem) {
        return new CartItemVm(cartItem.getProductId(), cartItem.getQuantity());
    }
}
