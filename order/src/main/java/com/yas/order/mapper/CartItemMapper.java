package com.yas.order.mapper;

import com.yas.order.model.CartItem;
import com.yas.order.viewmodel.cart.CartItemGetVm;
import com.yas.order.viewmodel.cart.CartItemPostVm;
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
}
