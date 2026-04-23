package com.yas.cart.mapper;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {
    public CartItemGetVm toGetVm(CartItem cartItem) {
        return CartItemGetVm
            .builder()
            .customerId(cartItem.getCustomerId())
            .productId(cartItem.getProductId())
            .quantity(cartItem.getQuantity())
            .build();
    }

    public CartItem toCartItem(CartItemPostVm cartItemPostVm, String currentUserId) {
        return CartItem
            .builder()
            .customerId(currentUserId)
            .productId(cartItemPostVm.productId())
            .quantity(cartItemPostVm.quantity())
            .build();
    }

    public CartItem toCartItem(String currentUserId, Long productId, int quantity) {
        return CartItem
            .builder()
            .customerId(currentUserId)
            .productId(productId)
            .quantity(quantity)
            .build();
    }

    public List<CartItemGetVm> toGetVms(List<CartItem> cartItems) {
        return cartItems
            .stream()
            .map(this::toGetVm)
            .toList();
    }
}