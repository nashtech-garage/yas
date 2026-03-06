package com.yas.cart.mapper;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapper();
    }

    @Test
    void testToGetVm_whenValidCartItem_shouldReturnCartItemGetVm() {
        // Given
        CartItem cartItem = CartItem.builder()
            .customerId("customer123")
            .productId(1L)
            .quantity(5)
            .build();

        // When
        CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

        // Then
        assertNotNull(result);
        assertEquals("customer123", result.customerId());
        assertEquals(1L, result.productId());
        assertEquals(5, result.quantity());
    }

    @Test
    void testToCartItem_whenValidCartItemPostVm_shouldReturnCartItem() {
        // Given
        CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
            .productId(2L)
            .quantity(3)
            .build();
        String currentUserId = "user456";

        // When
        CartItem result = cartItemMapper.toCartItem(cartItemPostVm, currentUserId);

        // Then
        assertNotNull(result);
        assertEquals("user456", result.getCustomerId());
        assertEquals(2L, result.getProductId());
        assertEquals(3, result.getQuantity());
    }

    @Test
    void testToCartItem_whenValidParameters_shouldReturnCartItem() {
        // Given
        String currentUserId = "user789";
        Long productId = 3L;
        int quantity = 7;

        // When
        CartItem result = cartItemMapper.toCartItem(currentUserId, productId, quantity);

        // Then
        assertNotNull(result);
        assertEquals("user789", result.getCustomerId());
        assertEquals(3L, result.getProductId());
        assertEquals(7, result.getQuantity());
    }

    @Test
    void testToGetVms_whenValidCartItemList_shouldReturnCartItemGetVmList() {
        // Given
        CartItem cartItem1 = CartItem.builder()
            .customerId("customer1")
            .productId(1L)
            .quantity(2)
            .build();

        CartItem cartItem2 = CartItem.builder()
            .customerId("customer1")
            .productId(2L)
            .quantity(4)
            .build();

        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);

        // When
        List<CartItemGetVm> result = cartItemMapper.toGetVms(cartItems);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        assertEquals("customer1", result.get(0).customerId());
        assertEquals(1L, result.get(0).productId());
        assertEquals(2, result.get(0).quantity());
        
        assertEquals("customer1", result.get(1).customerId());
        assertEquals(2L, result.get(1).productId());
        assertEquals(4, result.get(1).quantity());
    }

    @Test
    void testToGetVms_whenEmptyList_shouldReturnEmptyList() {
        // Given
        List<CartItem> emptyList = List.of();

        // When
        List<CartItemGetVm> result = cartItemMapper.toGetVms(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
