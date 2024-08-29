package com.yas.cart.controller;

import static com.yas.cart.util.SecurityContextUtils.setUpSecurityContext;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.cart.CartApplication;
import com.yas.cart.service.CartService;
import com.yas.cart.utils.Constants;
import com.yas.cart.viewmodel.CartGetDetailVm;
import com.yas.cart.viewmodel.CartItemPutVm;
import com.yas.cart.viewmodel.CartItemVm;
import com.yas.cart.viewmodel.CartListVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CartController.class)
@ContextConfiguration(classes = CartApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @MockBean
    private CartService cartService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testListCarts() throws Exception {

        CartListVm cartListVm = new CartListVm(1L, "1");
        List<CartListVm> cartList = Collections.singletonList(cartListVm);

        when(cartService.getCarts()).thenReturn(cartList);

        mockMvc.perform(MockMvcRequestBuilders.get("/backoffice/carts")
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(cartList)));
    }

    @Test
    void testListCartDetailByCustomerId() throws Exception {

        String customerId = "12345";
        CartGetDetailVm cartGetDetailVm = new CartGetDetailVm(1L, "2", 2L, List.of());
        List<CartGetDetailVm> cartDetailList = Collections.singletonList(cartGetDetailVm);

        when(cartService.getCartDetailByCustomerId(customerId)).thenReturn(cartDetailList);

        mockMvc.perform(MockMvcRequestBuilders.get("/backoffice/carts/{customerId}", customerId)
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(cartDetailList)));
    }

    @Test
    void testGetLastCartWithPrincipal() throws Exception {

        String username = "testUser";
        CartGetDetailVm cartGetDetailVm = new CartGetDetailVm(1L, "12", 2L, List.of());
        when(cartService.getLastCart(username)).thenReturn(cartGetDetailVm);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        mockMvc.perform(MockMvcRequestBuilders.get("/storefront/carts")
                .principal(principal)
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(cartGetDetailVm)));
    }

    @Test
    void testGetLastCartWithNoPrincipal() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/storefront/carts")
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void testCreateCartSuccess() throws Exception {

        List<CartItemVm> cartItemVms = List.of(new CartItemVm(2L, 5, 1L));
        CartGetDetailVm cartGetDetailVm = new CartGetDetailVm(1L, "1", 1L, List.of());

        when(cartService.addToCart(cartItemVms)).thenReturn(cartGetDetailVm);

        mockMvc.perform(MockMvcRequestBuilders.post("/storefront/carts")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(cartItemVms)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(cartGetDetailVm)));
    }

    @Test
    void testCreateCartNotFound() throws Exception {

        List<CartItemVm> cartItemVms = List.of(new CartItemVm(4L, 6, 1L));

        when(cartService.addToCart(cartItemVms))
            .thenThrow(new NotFoundException(Constants.ErrorCode.NOT_FOUND_PRODUCT));

        mockMvc.perform(MockMvcRequestBuilders.post("/storefront/carts")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(cartItemVms)))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testCreateCartBadRequest() throws Exception {

        List<CartItemVm> cartItemVms = List.of(new CartItemVm(3L, 4, 1L));

        when(cartService.addToCart(cartItemVms)).thenThrow(new BadRequestException("Not Found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/storefront/carts")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(cartItemVms)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUpdateCartSuccess() throws Exception {
        // Arrange
        CartItemVm cartItemVm = new CartItemVm(3L, 5, 1L);
        CartItemPutVm cartItemPutVm
            = new CartItemPutVm(1L, "2", 1L, 1, "Success");
        String username = "testUser1";

        when(cartService.updateCartItems(cartItemVm, username)).thenReturn(cartItemPutVm);

        setUpSecurityContext(username);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/cart-item")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(cartItemVm)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(cartItemPutVm)));
    }

    @Test
    void testRemoveCartItemSuccess() throws Exception {

        Long productId = 123L;
        String username = "testUser2";

        doNothing().when(cartService).removeCartItemByProductId(productId, username);

        setUpSecurityContext(username);

        mockMvc.perform(MockMvcRequestBuilders.delete("/storefront/cart-item")
                .param("productId", productId.toString())
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testRemoveCartItemListSuccess() throws Exception {

        List<Long> productIds = List.of(123L, 456L, 789L);
        String username = "testUser3";

        doNothing().when(cartService).removeCartItemListByProductIdList(productIds, username);

        setUpSecurityContext(username);

        mockMvc.perform(MockMvcRequestBuilders.delete("/storefront/cart-item/multi-delete")
                .param("productIds", productIds.stream().map(String::valueOf).toArray(String[]::new))
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testGetNumberItemInCartSuccess() throws Exception {

        String username = "testUser4";
        Long itemCount = 5L;

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(cartService.countNumberItemInCart(username)).thenReturn(itemCount);

        setUpSecurityContext(username);

        mockMvc.perform(MockMvcRequestBuilders.get("/storefront/count-cart-items")
                .principal(principal)
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(itemCount.toString()));
    }

    @Test
    void testGetNumberItemInCartWhenNoItems() throws Exception {

        String username = "testUser5";
        Long itemCount = 0L;

        when(cartService.countNumberItemInCart(username)).thenReturn(itemCount);

        setUpSecurityContext(username);

        mockMvc.perform(MockMvcRequestBuilders.get("/storefront/count-cart-items")
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(itemCount.toString()));
    }

}