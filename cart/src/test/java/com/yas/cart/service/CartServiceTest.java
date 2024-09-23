package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.cart.model.Cart;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.CartItemVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartServiceTest {

    private CartService cartService;
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        cartRepository = mock(CartRepository.class);
        cartItemRepository = mock(CartItemRepository.class);
        productService = mock(ProductService.class);
        cartService = new CartService(cartRepository, cartItemRepository, productService);
    }

    @Test
    void testAddToCart_ProductNotFound() {
        List<CartItemVm> cartItemVms = Collections.singletonList(new CartItemVm(1L, 2, null));
        when(productService.getProducts(anyList())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> cartService.addToCart(cartItemVms));
    }

    @Test
    void testUpdateCartItems_ItemNotFound() {
        String customerId = "customerId";
        CartItemVm cartItemVm = new CartItemVm(1L, 2, null);
        when(cartRepository.findByCustomerIdAndOrderIdIsNull(customerId))
            .thenReturn(Collections.singletonList(new Cart()));

        assertThrows(BadRequestException.class, () -> cartService.updateCartItems(cartItemVm, customerId));
    }

    @Test
    void testCountNumberItemInCart_EmptyCart() {
        String customerId = "customerId";
        when(cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)).thenReturn(Collections.emptyList());

        Long result = cartService.countNumberItemInCart(customerId);

        assertEquals(0L, result);
    }
}
