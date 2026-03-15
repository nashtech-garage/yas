package com.yas.cart.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapper();
    }

    @Test
    void toGetVm_shouldMapAllFields() {
        CartItem cartItem = CartItem.builder()
            .customerId("customer-1")
            .productId(10L)
            .quantity(3)
            .build();

        CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

        assertThat(result.customerId()).isEqualTo("customer-1");
        assertThat(result.productId()).isEqualTo(10L);
        assertThat(result.quantity()).isEqualTo(3);
    }

    @Test
    void toCartItem_withPostVm_shouldMapAllFields() {
        CartItemPostVm postVm = CartItemPostVm.builder()
            .productId(15L)
            .quantity(2)
            .build();

        CartItem result = cartItemMapper.toCartItem(postVm, "customer-2");

        assertThat(result.getCustomerId()).isEqualTo("customer-2");
        assertThat(result.getProductId()).isEqualTo(15L);
        assertThat(result.getQuantity()).isEqualTo(2);
    }

    @Test
    void toCartItem_withPrimitiveArgs_shouldMapAllFields() {
        CartItem result = cartItemMapper.toCartItem("customer-3", 20L, 5);

        assertThat(result.getCustomerId()).isEqualTo("customer-3");
        assertThat(result.getProductId()).isEqualTo(20L);
        assertThat(result.getQuantity()).isEqualTo(5);
    }

    @Test
    void toGetVms_shouldMapAllItems() {
        CartItem first = CartItem.builder().customerId("c1").productId(1L).quantity(1).build();
        CartItem second = CartItem.builder().customerId("c2").productId(2L).quantity(2).build();

        List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of(first, second));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).customerId()).isEqualTo("c1");
        assertThat(result.get(0).productId()).isEqualTo(1L);
        assertThat(result.get(0).quantity()).isEqualTo(1);
        assertThat(result.get(1).customerId()).isEqualTo("c2");
        assertThat(result.get(1).productId()).isEqualTo(2L);
        assertThat(result.get(1).quantity()).isEqualTo(2);
    }
}
