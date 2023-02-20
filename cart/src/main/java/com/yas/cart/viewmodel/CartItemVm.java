package com.yas.cart.viewmodel;

import com.yas.cart.model.CartItem;

import jakarta.validation.constraints.Min;

public record CartItemVm(Long productId, @Min(0) int quantity) {
    public static CartItemVm fromModel(CartItem cartItem) {
        return new CartItemVm(cartItem.getProductId(), cartItem.getQuantity());
    }
}
