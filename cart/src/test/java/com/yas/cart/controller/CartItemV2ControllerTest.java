package com.yas.cart.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.cart.service.CartItemV2Service;
import com.yas.cart.viewmodel.CartItemV2DeleteVm;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.cart.viewmodel.CartItemV2PutVm;
import com.yas.commonlibrary.exception.ApiExceptionHandler;
import java.util.List;
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
@ContextConfiguration(classes = {CartItemV2Controller.class, ApiExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CartItemV2ControllerTest {

    private static final Long PRODUCT_ID_SAMPLE = 1L;
    private static final String CUSTOMER_ID_SAMPLE = "customerId";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartItemV2Service cartItemService;

    @Nested
    class AddToCartTest {

        private CartItemV2PostVm.CartItemV2PostVmBuilder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemV2PostVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1);
        }

        @Test
        void testAddToCart_whenProductIdIsNull_shouldReturnBadRequest() throws Exception {
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.productId(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.quantity(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsLessThanOne_shouldReturnBadRequest() throws Exception {
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.quantity(0).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenRequestIsValid_shouldReturnCartItem() throws Exception {
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.build();
            CartItemV2GetVm expectedCartItem = CartItemV2GetVm.builder()
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

        private void performAddCartItemAndExpectBadRequest(CartItemV2PostVm cartItemPostVm)
            throws Exception {
            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildAddCartItemRequest(CartItemV2PostVm cartItemPostVm)
            throws Exception {
            return post("/storefront/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPostVm));
        }
    }

    @Nested
    class UpdateCartItemTest {

        private CartItemV2PutVm cartItemPutVm;

        @Test
        void testUpdateCartItem_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemV2PutVm(null);
            performUpdateCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testUpdateCartItem_whenQuantityIsLessThanOne_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemV2PutVm(0);
            performUpdateCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testUpdateCartItem_whenRequestIsValid_shouldReturnUpdatedCartItemGetVm() throws Exception {
            cartItemPutVm = new CartItemV2PutVm(1);
            CartItemV2GetVm expectedCartItemGetVm = CartItemV2GetVm
                .builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.updateCartItem(anyLong(), any())).thenReturn(expectedCartItemGetVm);

            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(expectedCartItemGetVm.customerId()))
                .andExpect(jsonPath("$.productId").value(expectedCartItemGetVm.productId()))
                .andExpect(jsonPath("$.quantity").value(expectedCartItemGetVm.quantity()));

            verify(cartItemService).updateCartItem(anyLong(), any());
        }

        private void performUpdateCartItemAndExpectBadRequest(CartItemV2PutVm cartItemPutVm)
            throws Exception {
            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildUpdateCartItemRequest(Long productId, CartItemV2PutVm cartItemPutVm)
            throws Exception {
            return put("/storefront/cart/items/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPutVm));
        }
    }

    @Nested
    class GetCartItemsTest {

        @Test
        void testGetCartItems_whenRequestIsValid_shouldReturnCartItems() throws Exception {
            CartItemV2GetVm expectedCartItem = CartItemV2GetVm.builder()
                .productId(1L)
                .quantity(1)
                .build();

            when(cartItemService.getCartItems()).thenReturn(List.of(expectedCartItem));

            mockMvc.perform(get("/storefront/cart/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(expectedCartItem.productId()))
                .andExpect(jsonPath("$[0].quantity").value(expectedCartItem.quantity()));

            verify(cartItemService).getCartItems();
        }
    }

    @Nested
    class DeleteOrAdjustCartItemTest {

        private CartItemV2DeleteVm cartItemPutVm;

        @Test
        void testDeleteOrAdjustCartItem_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemV2DeleteVm(PRODUCT_ID_SAMPLE, null);
            performDeleteOrAdjustCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testDeleteOrAdjustCartItem_whenQuantityIsLessThanOne_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemV2DeleteVm(PRODUCT_ID_SAMPLE, -1);
            performDeleteOrAdjustCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testDeleteOrAdjustCartItem_whenRequestIsValid_shouldReturnUpdatedCartItems() throws Exception {
            CartItemV2DeleteVm cartItemDeleteVm = new CartItemV2DeleteVm(PRODUCT_ID_SAMPLE, 1);
            CartItemV2GetVm expectedCartItemGetVm = CartItemV2GetVm
                .builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.deleteOrAdjustCartItem(anyList())).thenReturn(List.of(expectedCartItemGetVm));

            mockMvc.perform(buildDeleteOrAdjustCartItemRequest(cartItemDeleteVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(expectedCartItemGetVm.customerId()))
                .andExpect(jsonPath("$[0].productId").value(expectedCartItemGetVm.productId()))
                .andExpect(jsonPath("$[0].quantity").value(expectedCartItemGetVm.quantity()));

            verify(cartItemService).deleteOrAdjustCartItem(anyList());
        }

        private void performDeleteOrAdjustCartItemAndExpectBadRequest(CartItemV2DeleteVm cartItemDeleteVm)
            throws Exception {
            mockMvc.perform(buildDeleteOrAdjustCartItemRequest(cartItemDeleteVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildDeleteOrAdjustCartItemRequest(CartItemV2DeleteVm cartItemDeleteVm)
            throws Exception {
            return post("/storefront/cart/items/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(cartItemDeleteVm)));
        }
    }
}