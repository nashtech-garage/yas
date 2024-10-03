package com.yas.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.order.exception.ApiExceptionHandler;
import com.yas.order.service.CartItemService;
import com.yas.order.viewmodel.cart.CartItemPostVm;
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

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {CartItemController.class, ApiExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CartItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartItemService cartItemService;

    @Nested
    class AddToCartTest {

        private CartItemPostVm.CartItemPostVmBuilder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemPostVm.builder()
                    .productId(1L)
                    .quantity(1);
        }

        @Test
        void testAddToCart_whenProductIdIsNull_thenReturnBadRequest() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.productId(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsNull_thenReturnBadRequest() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.quantity(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsLessThanOne_thenReturnBadRequest() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.quantity(0).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        private void performAddCartItemAndExpectBadRequest(CartItemPostVm cartItemPostVm)
            throws Exception {
            mockMvc.perform(post("/storefront/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cartItemPostVm)))
                .andExpect(status().isBadRequest());
        }

        @Test
        void testAddToCart_whenRequestIsValid_thenReturnSuccess() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();

            doNothing().when(cartItemService).addCartItem(any(CartItemPostVm.class));

            mockMvc.perform(post("/storefront/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cartItemPostVm)))
                .andExpect(status().isOk());

            verify(cartItemService).addCartItem(cartItemPostVm);
        }
    }

}
