package com.yas.cart.viewmodel;

import com.yas.cart.model.CartDetail;

public record CartDetailPostVM(Long productId, int quantity) {
    public static CartDetailPostVM fromModel(CartDetail cartDetail) {
        return new CartDetailPostVM(cartDetail.getProductId(), cartDetail.getQuantity());
    }
}
