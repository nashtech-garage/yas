package com.yas.cart.viewmodel;

import com.yas.cart.model.CartDetail;

public record CartDetailListVm(Long id, Long productId, int quantity) {
    public static CartDetailListVm fromModel(CartDetail cartDetail) {
        return new CartDetailListVm(cartDetail.getId(), cartDetail.getProductId(), cartDetail.getQuantity());
    }
}
