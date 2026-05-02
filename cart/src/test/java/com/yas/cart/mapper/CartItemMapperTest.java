package com.yas.cart.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import java.util.List;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {
    private final CartItemMapper cartItemMapper = new CartItemMapper();

    @Test
    void toGetVm_ValidCartItem_ReturnsCartItemGetVm() {
        CartItem cartItem = CartItem.builder()
            .customerId("user1")
            .productId(100L)
            .quantity(5)
            .build();

        CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

        assertNotNull(result);
        assertEquals(cartItem.getCustomerId(), result.customerId());
        assertEquals(cartItem.getProductId(), result.productId());
        assertEquals(cartItem.getQuantity(), result.quantity());
    }

    @Test
    void toCartItem_FromPostVm_ReturnsCartItem() {
        CartItemPostVm postVm = CartItemPostVm.builder()
            .productId(200L)
            .quantity(2)
            .build();
        String userId = "user2";

        CartItem result = cartItemMapper.toCartItem(postVm, userId);

        assertNotNull(result);
        assertEquals(userId, result.getCustomerId());
        assertEquals(postVm.productId(), result.getProductId());
        assertEquals(postVm.quantity(), result.getQuantity());
    }

    @Test
    void toCartItem_FromParams_ReturnsCartItem() {
        String userId = "user3";
        Long productId = 300L;
        int quantity = 10;

        CartItem result = cartItemMapper.toCartItem(userId, productId, quantity);

        assertNotNull(result);
        assertEquals(userId, result.getCustomerId());
        assertEquals(productId, result.getProductId());
        assertEquals(quantity, result.getQuantity());
    }

    @Test
    void toGetVms_List_ReturnsList() {
        CartItem item1 = CartItem.builder().customerId("u1").productId(1L).quantity(1).build();
        CartItem item2 = CartItem.builder().customerId("u1").productId(2L).quantity(2).build();
        List<CartItem> cartItems = List.of(item1, item2);

        List<CartItemGetVm> result = cartItemMapper.toGetVms(cartItems);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1.getProductId(), result.get(0).productId());
        assertEquals(item2.getProductId(), result.get(1).productId());
    }
}
