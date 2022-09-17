package com.yas.cart.viewmodel;

import java.util.List;

import com.yas.cart.model.Cart;

public record CartGetDetailVM(Long id, String customerId, List<CartDetailListVM> cartDetails) {
    public static CartGetDetailVM fromModel(Cart cart) {
        return new CartGetDetailVM(cart.getId(), cart.getCustomerId(),
        cart.getCartDetails().stream().map(CartDetailListVM::fromModel).toList());
    }
}
