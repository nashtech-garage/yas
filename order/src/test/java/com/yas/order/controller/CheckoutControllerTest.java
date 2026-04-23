package com.yas.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.service.CheckoutService;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;

import com.yas.order.viewmodel.checkout.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = CheckoutController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutControllerTest {

    @MockitoBean
    private CheckoutService checkoutService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreateCheckout_whenRequestIsValid_thenReturnCheckoutVm() throws Exception {

        CheckoutVm response = getCheckoutVm();
        when(checkoutService.createCheckout(any(CheckoutPostVm.class))).thenReturn(response);

        List<CheckoutItemPostVm> items = getCheckoutItemPostVms();
        CheckoutPostVm request = new CheckoutPostVm(
                "customer@example.com",
                "Please deliver before noon.",
                "SUMMER2024",
                null, null, null,
                items
        );

        mockMvc.perform(post("/storefront/checkouts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(response)));

    }

    @Test
    void testUpdateCheckoutStatus_whenRequestIsValid_thenReturnLong() throws Exception {

        CheckoutStatusPutVm request = new CheckoutStatusPutVm("1", "2");
        Long response = 123L;
        when(checkoutService.updateCheckoutStatus(any(CheckoutStatusPutVm.class))).thenReturn(response);

        mockMvc.perform(put("/storefront/checkouts/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(response)));
    }

    @Test
    void testGetOrderWithItemsById_whenRequestIsValid_thenReturnCheckoutVm() throws Exception {

        String id = "123";
        CheckoutVm response = getCheckoutVm();
        when(checkoutService.getCheckoutPendingStateWithItemsById(id)).thenReturn(response);

        mockMvc.perform(get("/storefront/checkouts/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(response)));
    }

    @Test
    void testUpdatePaymentMethod_whenRequestIsValid_thenReturnNoContent() throws Exception {
        String id = "123";
        CheckoutPaymentMethodPutVm request = new CheckoutPaymentMethodPutVm("12hgds1");

        doNothing().when(checkoutService).updateCheckoutPaymentMethod(id, request);

        mockMvc.perform(put("/storefront/checkouts/{id}/payment-method", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(checkoutService).updateCheckoutPaymentMethod(id, request);
    }

    private static @NotNull
    List<CheckoutItemPostVm> getCheckoutItemPostVms() {
        CheckoutItemPostVm item1 = new CheckoutItemPostVm(
                101L,
                "First item note",
                3
        );

        CheckoutItemPostVm item2 = new CheckoutItemPostVm(
                102L,
                "Second item note",
                1
        );

        return List.of(item1, item2);
    }

    private CheckoutVm getCheckoutVm() {
        CheckoutItemVm item1 = CheckoutItemVm.builder()
                .id(1L)
                .productId(101L)
                .productName("Product 1")
                .quantity(2)
                .productPrice(new BigDecimal("19.99"))
                .description("First item description")
                .discountAmount(new BigDecimal("2.00"))
                .taxAmount(new BigDecimal("1.50"))
                .checkoutId("checkout123")
                .build();

        CheckoutItemVm item2 = CheckoutItemVm.builder()
                .id(2L)
                .productId(102L)
                .productName("Product 2")
                .quantity(1)
                .productPrice(new BigDecimal("9.99"))
                .description("Second item description")
                .discountAmount(new BigDecimal("1.00"))
                .taxAmount(new BigDecimal("0.75"))
                .checkoutId("checkout123")
                .build();

        Set<CheckoutItemVm> checkoutItemVms = new HashSet<>();
        checkoutItemVms.add(item1);
        checkoutItemVms.add(item2);

        return new CheckoutVm(
                "014476b3-243a-4111-9f2a-a25661aea89c",
                "user@example.com",
                "Please deliver after 5 PM",
                "DISCOUNT20",
                CheckoutState.CHECKED_OUT,
                "Inprogress",
                BigDecimal.valueOf(900),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null, null, null,
                checkoutItemVms
        );
    }
}