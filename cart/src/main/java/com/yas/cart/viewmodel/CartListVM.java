package com.yas.cart.viewmodel;

import com.yas.cart.model.Cart;

public record CartListVM(Long id, String customerId) {
    public static CartListVM fromModel(Cart cart) {
        return new CartListVM(cart.getId(), cart.getCustomerId());
    }
}
