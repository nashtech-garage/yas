package com.yas.cart.viewmodel;

import com.yas.cart.model.CartItem;

public record CartDetailListVm(Long id, Long productId, int quantity) {
    public static CartDetailListVm fromModel(CartItem cartItem) {
        return new CartDetailListVm(cartItem.getId(), cartItem.getProductId(), cartItem.getQuantity());
    }
}
