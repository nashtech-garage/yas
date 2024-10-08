package com.yas.cart.mapper;

import com.yas.cart.model.CartItemV2;
import com.yas.cart.viewmodel.CartItemGetVmV2;
import com.yas.cart.viewmodel.CartItemPostVmV2;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapperV2 {
    public CartItemGetVmV2 toGetVm(CartItemV2 cartItem) {
        return CartItemGetVmV2
            .builder()
            .customerId(cartItem.getCustomerId())
            .productId(cartItem.getProductId())
            .quantity(cartItem.getQuantity())
            .build();
    }

    public CartItemV2 toCartItem(CartItemPostVmV2 cartItemPostVm, String currentUserId) {
        return CartItemV2
            .builder()
            .customerId(currentUserId)
            .productId(cartItemPostVm.productId())
            .quantity(cartItemPostVm.quantity())
            .build();
    }

    public CartItemV2 toCartItem(String currentUserId, Long productId, int quantity) {
        return CartItemV2
            .builder()
            .customerId(currentUserId)
            .productId(productId)
            .quantity(quantity)
            .build();
    }
}
