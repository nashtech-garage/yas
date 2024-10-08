package com.yas.cart.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.cart.service.CartItemServiceV2;
import com.yas.cart.viewmodel.CartItemGetVmV2;
import com.yas.cart.viewmodel.CartItemPostVmV2;
import com.yas.commonlibrary.exception.ApiExceptionHandler;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {CartItemControllerV2.class, ApiExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CartItemControllerV2Test {

    private static final String CART_ITEM_BASE_URL = "/storefront/cart/items";
    private static final String ADD_CART_ITEM_URL = CART_ITEM_BASE_URL;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartItemServiceV2 cartItemService;

    @Nested
    class AddToCartTest {

        private CartItemPostVmV2.CartItemPostVmV2Builder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemPostVmV2.builder()
                .productId(1L)
                .quantity(1);
        }

        @Test
        void testAddToCart_whenProductIdIsNull_shouldReturnBadRequest() throws Exception {
            CartItemPostVmV2 cartItemPostVm = cartItemPostVmBuilder.productId(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
            CartItemPostVmV2 cartItemPostVm = cartItemPostVmBuilder.quantity(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsLessThanOne_shouldReturnBadRequest() throws Exception {
            CartItemPostVmV2 cartItemPostVm = cartItemPostVmBuilder.quantity(0).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenRequestIsValid_shouldReturnCartItem() throws Exception {
            CartItemPostVmV2 cartItemPostVm = cartItemPostVmBuilder.build();
            CartItemGetVmV2 expectedCartItem = CartItemGetVmV2.builder()
                .productId(cartItemPostVm.productId())
                .quantity(cartItemPostVm.quantity())
                .build();

            when(cartItemService.addCartItem(cartItemPostVm)).thenReturn(expectedCartItem);

            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(expectedCartItem.productId()))
                .andExpect(jsonPath("$.quantity").value(expectedCartItem.quantity()));

            verify(cartItemService).addCartItem(cartItemPostVm);
        }

        private void performAddCartItemAndExpectBadRequest(CartItemPostVmV2 cartItemPostVm)
            throws Exception {
            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildAddCartItemRequest(CartItemPostVmV2 cartItemPostVm)
            throws Exception {
            return post(ADD_CART_ITEM_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPostVm));
        }
    }
}
