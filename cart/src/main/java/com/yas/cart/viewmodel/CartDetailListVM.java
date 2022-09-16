package com.yas.cart.viewmodel;

import com.yas.cart.model.CartDetail;

public record CartDetailListVM(Long id, Long productID, int Quantity) {
    public static CartDetailListVM fromModel(CartDetail cartDetail) {
        return new CartDetailListVM(cartDetail.getId(), cartDetail.getProductID(), cartDetail.getQuantity());
    }
}
