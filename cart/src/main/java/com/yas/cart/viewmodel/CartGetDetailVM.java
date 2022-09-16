package com.yas.cart.viewmodel;

import java.util.List;

import com.yas.cart.model.Cart;

public record CartGetDetailVM(Long id, String customerID, List<CartDetailListVM> cartDetails) {
    public static CartGetDetailVM fromModel(Cart cart) {
        return new CartGetDetailVM(cart.getId(), cart.getCustomerID(),
        cart.getCartDetails().stream().map(CartDetailListVM::fromModel).toList());
    }
}
