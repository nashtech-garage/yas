package com.yas.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.order.exception.ApiExceptionHandler;
import com.yas.order.service.CartItemService;
import com.yas.order.viewmodel.cart.CartItemGetVm;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import com.yas.order.viewmodel.cart.CartItemPutVm;
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
@ContextConfiguration(classes = {CartItemController.class, ApiExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CartItemControllerTest {

    private static final String CART_ITEM_BASE_URL = "/storefront/cart/items";
    private static final String ADD_CART_ITEM_URL = CART_ITEM_BASE_URL;
    private static final String UPDATE_CART_ITEM_TEMPLATE = CART_ITEM_BASE_URL + "/%d";

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

        @Test
        void testAddToCart_whenRequestIsValid_thenReturnSuccess() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();

            doNothing().when(cartItemService).addCartItem(any(CartItemPostVm.class));

            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isOk());

            verify(cartItemService).addCartItem(cartItemPostVm);
        }

        private void performAddCartItemAndExpectBadRequest(CartItemPostVm cartItemPostVm)
            throws Exception {
            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildAddCartItemRequest(CartItemPostVm cartItemPostVm)
            throws Exception {
            return post(ADD_CART_ITEM_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPostVm));
        }
    }

    @Nested
    class UpdateCartItemTest {

        private static final Long PRODUCT_ID_SAMPLE = 1L;

        @Test
        void testUpdateCartItem_whenQuantityIsNull_thenReturnBadRequest() throws Exception {
            CartItemPutVm cartItemPutVm = CartItemPutVm.builder().quantity(null).build();
            performUpdateCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testUpdateCartItem_whenQuantityIsLessThanOne_thenReturnBadRequest() throws Exception {
            CartItemPutVm cartItemPutVm = CartItemPutVm.builder().quantity(0).build();
            performUpdateCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testUpdateCartItem_whenRequestIsValid_thenReturnUpdatedCartItemGetVm() throws Exception {
            CartItemPutVm cartItemPutVm = CartItemPutVm.builder().quantity(1).build();
            CartItemGetVm expectedCartItemGetVm = CartItemGetVm
                .builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .customerId("customerId")
                .build();

            when(cartItemService.updateCartItem(anyLong(), any())).thenReturn(expectedCartItemGetVm);

            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID_SAMPLE))
                .andExpect(jsonPath("$.quantity").value(cartItemPutVm.quantity()));

            verify(cartItemService).updateCartItem(anyLong(), any());
        }

        private void performUpdateCartItemAndExpectBadRequest(CartItemPutVm cartItemPutVm)
            throws Exception {
            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildUpdateCartItemRequest(Long productId, CartItemPutVm cartItemPutVm)
            throws Exception {
            return put(getUpdateCartItemUrl(productId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPutVm));
        }

        private String getUpdateCartItemUrl(Long productId) {
            return String.format(UPDATE_CART_ITEM_TEMPLATE, productId);
        }
    }
}
