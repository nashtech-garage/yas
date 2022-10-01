package com.yas.cart.viewmodel;

import com.yas.cart.model.CartDetail;

public record CartItemVm(Long productId, int quantity) {
    public static CartItemVm fromModel(CartDetail cartDetail) {
        return new CartItemVm(cartDetail.getProductId(), cartDetail.getQuantity());
    }
}
