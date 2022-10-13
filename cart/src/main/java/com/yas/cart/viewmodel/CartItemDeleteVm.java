package com.yas.cart.viewmodel;

import com.yas.cart.model.CartItem;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public record CartItemDeleteVm(Long cartItemId, String userId, Long productId, Integer quantity, String status) {

    public static CartItemDeleteVm fromModel(CartItem cartItem, String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerId = auth.getName();
        return new CartItemDeleteVm(cartItem.getId(), customerId, cartItem.getProductId(), cartItem.getQuantity(), status);
    }
}
