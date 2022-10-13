package com.yas.cart.viewmodel;

import com.yas.cart.model.CartItem;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public record CartItemPutVm(Long cartItemId, String userId, Long productId, Integer quantity, String status) {

    public static CartItemPutVm fromModel(CartItem cartItem, String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerId = auth.getName();
        return new CartItemPutVm(cartItem.getId(), customerId, cartItem.getProductId(), cartItem.getQuantity(), status);
    }
}
