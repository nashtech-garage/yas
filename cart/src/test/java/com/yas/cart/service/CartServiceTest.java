package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.yas.cart.model.Cart;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.CartItemVm;
import com.yas.cart.viewmodel.CartListVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductService productService;
    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCarts_thenReturnCartsList() {
        List<Cart> cartList = Collections.singletonList(new Cart(1L, "customerId", null, new HashSet<>()));
        when(cartRepository.findAll()).thenReturn(cartList);

        List<CartListVm> cartItemVms = cartService.getCarts();

        assertEquals(cartList.size(), cartItemVms.size());
        assertEquals(cartList.getFirst().getId(), cartItemVms.getFirst().id());
    }

    @Test
    void testAddToCart_whenProductNotFound_thenThrowNotFoundException() {
        List<CartItemVm> cartItemVms = Collections.singletonList(new CartItemVm(1L, 2, null));
        when(productService.getProducts(anyList())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> cartService.addToCart(cartItemVms));
    }

    @Test
    void testUpdateCartItems_whenCartItemNotExisted_thenThrowBadRequestException() {
        String customerId = "customerId";
        CartItemVm cartItemVm = new CartItemVm(1L, 2, null);
        when(cartRepository.findByCustomerIdAndOrderIdIsNull(customerId))
            .thenReturn(Collections.singletonList(new Cart()));

        assertThrows(BadRequestException.class, () -> cartService.updateCartItems(cartItemVm, customerId));
    }

    @Test
    void testCountNumberItemInCart_whenEmptyCart_thenReturnZero() {
        String customerId = "customerId";
        when(cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)).thenReturn(Collections.emptyList());

        Long result = cartService.countNumberItemInCart(customerId);

        assertEquals(0L, result);
    }
}

