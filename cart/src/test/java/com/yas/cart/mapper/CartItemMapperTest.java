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

    private static final String CUSTOMER_ID = "user-001";
    private static final Long PRODUCT_ID = 42L;
    private static final int QUANTITY = 3;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapper();
    }

    @Nested
    class ToGetVmTest {

        @Test
        void toGetVm_whenCartItemIsValid_shouldMapAllFields() {
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

        @Test
        void toGetVm_whenQuantityIsOne_shouldMapCorrectly() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID)
                .productId(PRODUCT_ID)
                .quantity(1)
                .build();

            CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

            assertEquals(1, result.quantity());
        }
    }

    @Nested
    class ToCartItemFromPostVmTest {

        @Test
        void toCartItem_fromPostVm_whenInputIsValid_shouldMapAllFields() {
            CartItemPostVm postVm = CartItemPostVm.builder()
                .productId(PRODUCT_ID)
                .quantity(QUANTITY)
                .build();

            CartItem result = cartItemMapper.toCartItem(postVm, CUSTOMER_ID);

            assertNotNull(result);
            assertEquals(CUSTOMER_ID, result.getCustomerId());
            assertEquals(PRODUCT_ID, result.getProductId());
            assertEquals(QUANTITY, result.getQuantity());
        }

        @Test
        void toCartItem_fromPostVm_whenQuantityIsMin_shouldMapCorrectly() {
            CartItemPostVm postVm = CartItemPostVm.builder()
                .productId(PRODUCT_ID)
                .quantity(1)
                .build();

            CartItem result = cartItemMapper.toCartItem(postVm, CUSTOMER_ID);

            assertEquals(1, result.getQuantity());
            assertEquals(CUSTOMER_ID, result.getCustomerId());
        }
    }

    @Nested
    class ToCartItemFromParamsTest {

        @Test
        void toCartItem_fromParams_whenInputIsValid_shouldMapAllFields() {
            CartItem result = cartItemMapper.toCartItem(CUSTOMER_ID, PRODUCT_ID, QUANTITY);

            assertNotNull(result);
            assertEquals(CUSTOMER_ID, result.getCustomerId());
            assertEquals(PRODUCT_ID, result.getProductId());
            assertEquals(QUANTITY, result.getQuantity());
        }

        @Test
        void toCartItem_fromParams_whenQuantityIsDifferent_shouldMapCorrectly() {
            int differentQuantity = 10;
            CartItem result = cartItemMapper.toCartItem(CUSTOMER_ID, PRODUCT_ID, differentQuantity);

            assertEquals(differentQuantity, result.getQuantity());
        }
    }

    @Nested
    class ToGetVmsTest {

        @Test
        void toGetVms_whenListHasMultipleItems_shouldMapAll() {
            CartItem cartItem1 = CartItem.builder()
                .customerId(CUSTOMER_ID)
                .productId(1L)
                .quantity(1)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId(CUSTOMER_ID)
                .productId(2L)
                .quantity(2)
                .build();
            CartItem cartItem3 = CartItem.builder()
                .customerId("other-user")
                .productId(3L)
                .quantity(5)
                .build();

            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of(cartItem1, cartItem2, cartItem3));

            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(1L, result.get(0).productId());
            assertEquals(2L, result.get(1).productId());
            assertEquals(3L, result.get(2).productId());
            assertEquals(5, result.get(2).quantity());
        }

        @Test
        void toGetVms_whenListIsEmpty_shouldReturnEmptyList() {
            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of());

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void toGetVms_whenListHasSingleItem_shouldReturnSingleElement() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID)
                .productId(PRODUCT_ID)
                .quantity(QUANTITY)
                .build();

            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of(cartItem));

            assertEquals(1, result.size());
            assertEquals(CUSTOMER_ID, result.getFirst().customerId());
            assertEquals(PRODUCT_ID, result.getFirst().productId());
            assertEquals(QUANTITY, result.getFirst().quantity());
        }
    }
}
