package com.yas.cart.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    private static final String CUSTOMER_ID = "customer-123";
    private static final Long PRODUCT_ID = 1L;
    private static final int QUANTITY = 3;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapper();
    }

    @Nested
    class ToGetVmTest {

        @Test
        void testToGetVm_shouldMapAllFieldsCorrectly() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID)
                .productId(PRODUCT_ID)
                .quantity(QUANTITY)
                .build();

            CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

            assertNotNull(result);
            assertEquals(CUSTOMER_ID, result.customerId());
            assertEquals(PRODUCT_ID, result.productId());
            assertEquals(QUANTITY, result.quantity());
        }
    }

    @Nested
    class ToCartItemFromPostVmTest {

        @Test
        void testToCartItem_fromPostVm_shouldMapAllFieldsCorrectly() {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(PRODUCT_ID)
                .quantity(QUANTITY)
                .build();

            CartItem result = cartItemMapper.toCartItem(cartItemPostVm, CUSTOMER_ID);

            assertNotNull(result);
            assertEquals(CUSTOMER_ID, result.getCustomerId());
            assertEquals(PRODUCT_ID, result.getProductId());
            assertEquals(QUANTITY, result.getQuantity());
        }
    }

    @Nested
    class ToCartItemFromParamsTest {

        @Test
        void testToCartItem_fromParams_shouldMapAllFieldsCorrectly() {
            CartItem result = cartItemMapper.toCartItem(CUSTOMER_ID, PRODUCT_ID, QUANTITY);

            assertNotNull(result);
            assertEquals(CUSTOMER_ID, result.getCustomerId());
            assertEquals(PRODUCT_ID, result.getProductId());
            assertEquals(QUANTITY, result.getQuantity());
        }
    }

    @Nested
    class ToGetVmsTest {

        @Test
        void testToGetVms_shouldReturnListWithCorrectSize() {
            CartItem cartItem1 = CartItem.builder().customerId(CUSTOMER_ID).productId(1L).quantity(1).build();
            CartItem cartItem2 = CartItem.builder().customerId(CUSTOMER_ID).productId(2L).quantity(2).build();

            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of(cartItem1, cartItem2));

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).productId());
            assertEquals(2L, result.get(1).productId());
        }

        @Test
        void testToGetVms_whenEmpty_shouldReturnEmptyList() {
            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of());

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }
}
