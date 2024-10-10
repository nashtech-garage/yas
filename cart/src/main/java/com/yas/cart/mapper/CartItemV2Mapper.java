package com.yas.cart.mapper;

import com.yas.cart.model.CartItemV2;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CartItemV2Mapper {
    public CartItemV2GetVm toGetVm(CartItemV2 cartItem) {
        return CartItemV2GetVm
            .builder()
            .customerId(cartItem.getCustomerId())
            .productId(cartItem.getProductId())
            .quantity(cartItem.getQuantity())
            .build();
    }

    public CartItemV2 toCartItem(CartItemV2PostVm cartItemPostVm, String currentUserId) {
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

    public List<CartItemV2GetVm> toGetVmList(List<CartItemV2> cartItems) {
        return cartItems.stream().map(this::toGetVm).toList();
    }
}
