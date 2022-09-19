package com.yas.cart.viewmodel;

import com.yas.cart.model.CartDetail;

public record CartDetailListVM(Long id, Long productId, int Quantity) {
    public static CartDetailListVM fromModel(CartDetail cartDetail) {
        return new CartDetailListVM(cartDetail.getId(), cartDetail.getProductId(), cartDetail.getQuantity());
    }
}
