package com.yas.cart.service;

import com.yas.cart.CartApplication;
import com.yas.cart.model.Cart;
import com.yas.cart.model.CartItem;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.CartListVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CartApplication.class)
class CartServiceTest {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @MockBean
    private ProductService productService;
    @Autowired
    private CartService cartService;
    Cart cart1;
    Cart cart2;

    @BeforeEach
    void setUp() {
        cart1 = cartRepository.save(Cart
                .builder().customerId("customer-1").build());
        cart2 = cartRepository.save(Cart
                .builder().customerId("customer-2").build());

        CartItem cartItem1 = new CartItem();
        cartItem1.setProductId(1L);
        cartItem1.setQuantity(2);
        cartItem1.setCart(cart1);
        CartItem cartItem2 = new CartItem();
        cartItem2.setProductId(2L);
        cartItem2.setQuantity(3);
        cartItem2.setCart(cart2);

        cartItemRepository.saveAll(List.of(cartItem1, cartItem2));
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    void getCarts_ExistInDatabase_Success() {
        List<CartListVm> cartListVmActual = cartService.getCarts();
        assertEquals(2, cartListVmActual.size());
        for(CartListVm cartListVm : cartListVmActual) {
            assertThat(cartListVm.customerId().startsWith("customer-"));
        }
    }
}
