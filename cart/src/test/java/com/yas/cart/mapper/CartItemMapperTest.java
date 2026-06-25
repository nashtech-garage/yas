package com.yas.cart.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapper();
    }

    @Nested
    class ToGetVmTest {

        @Test
        void testToGetVm_whenValidCartItem_shouldReturnCorrectVm() {
            CartItem cartItem = CartItem.builder()
                .customerId("user1")
                .productId(1L)
                .quantity(5)
                .build();

            CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

            assertNotNull(result);
            assertEquals("user1", result.customerId());
            assertEquals(1L, result.productId());
            assertEquals(5, result.quantity());
        }
    }

    @Nested
    class ToCartItemFromPostVmTest {

        @Test
        void testToCartItem_whenValidPostVm_shouldReturnCorrectCartItem() {
            CartItemPostVm postVm = CartItemPostVm.builder()
                .productId(2L)
                .quantity(3)
                .build();

            CartItem result = cartItemMapper.toCartItem(postVm, "user1");

            assertNotNull(result);
            assertEquals("user1", result.getCustomerId());
            assertEquals(2L, result.getProductId());
            assertEquals(3, result.getQuantity());
        }
    }

    @Nested
    class ToCartItemFromParamsTest {

        @Test
        void testToCartItem_withIndividualParams_shouldReturnCorrectCartItem() {
            CartItem result = cartItemMapper.toCartItem("user1", 3L, 10);

            assertNotNull(result);
            assertEquals("user1", result.getCustomerId());
            assertEquals(3L, result.getProductId());
            assertEquals(10, result.getQuantity());
        }
    }

    @Nested
    class ToGetVmsTest {

        @Test
        void testToGetVms_whenValidList_shouldReturnMappedList() {
            CartItem item1 = CartItem.builder().customerId("u1").productId(1L).quantity(2).build();
            CartItem item2 = CartItem.builder().customerId("u2").productId(2L).quantity(4).build();

            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of(item1, item2));

            assertEquals(2, result.size());
            assertEquals("u1", result.get(0).customerId());
            assertEquals("u2", result.get(1).customerId());
        }

        @Test
        void testToGetVms_whenEmptyList_shouldReturnEmptyList() {
            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of());

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
