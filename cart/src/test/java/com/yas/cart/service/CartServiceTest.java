package com.yas.cart.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.cart.model.Cart;
import com.yas.cart.model.CartDetail;
import com.yas.cart.repository.CartDetailRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.CartGetDetailVm;
import com.yas.cart.viewmodel.CartListVm;

public class CartServiceTest {
    
    CartRepository cartRepository;
    CartDetailRepository cartDetailRepository;
    CartService cartService;
    ProductService productService;

    CartGetDetailVm cartGetDetailVm;
    Cart cart1;
    Cart cart2;
    List<Cart> carts;

    Authentication authentication;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        cartDetailRepository = mock(CartDetailRepository.class);
        productService = mock(ProductService.class);
        cartService = new CartService(
                cartRepository,
                cartDetailRepository,
                productService);

        cartGetDetailVm = new CartGetDetailVm(1L, "customerId", null);
        
        List<CartDetail> cartDetailList = List.of(
            new CartDetail(1L, null, 1L, null, 1),
            new CartDetail(2L, null, 2L, null, 2)
        );
        cart1 = new Cart(1L, "customer-1", cartDetailList);
        cart2 = new Cart(2L, "customer-2", null);
        carts = List.of(cart1, cart2);
       
        //Security config
        authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("Name");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getCarts_ExistProductsInDatabase_Sucsess() {
        //given
        List<CartListVm> cartListVmExpected = List.of(
                new CartListVm(1L, "customer-1"),
                new CartListVm(2L, "customer-2")
        );
        when(cartRepository.findAll()).thenReturn(carts);

        //when
        List<CartListVm> cartListVmActual = cartService.getCarts();

        //then
        assertThat(cartListVmActual).hasSameSizeAs(cartListVmExpected);
        assertThat(cartListVmActual.get(0)).isEqualTo(cartListVmExpected.get(0));
        assertThat(cartListVmActual.get(1)).isEqualTo(cartListVmExpected.get(1));

    }
}
