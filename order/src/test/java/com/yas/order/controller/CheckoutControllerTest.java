package com.yas.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.order.OrderApplication;
import com.yas.order.service.CheckoutService;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CheckoutController.class)
@ContextConfiguration(classes = OrderApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutControllerTest {

    @MockBean
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
        CheckoutPostVm request  = new CheckoutPostVm(
            "customer@example.com",
            "Please deliver before noon.",
            "SUMMER2024",
            items
        );

        mockMvc.perform(post("/storefront/checkouts")
                .contentType(MediaType.APPLICATION_JSON)
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


    private static @NotNull List<CheckoutItemPostVm> getCheckoutItemPostVms() {
        CheckoutItemPostVm item1 = new CheckoutItemPostVm(
            101L,
            "Product One",
            3,
            new BigDecimal("29.99"),
            "First item note",
            new BigDecimal("5.00"),
            new BigDecimal("2.50"),
            new BigDecimal("8.5")
        );

        CheckoutItemPostVm item2 = new CheckoutItemPostVm(
            102L,
            "Product Two",
            1,
            new BigDecimal("49.99"),
            "Second item note",
            new BigDecimal("10.00"),
            new BigDecimal("5.00"),
            new BigDecimal("10.0")
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
            .note("First item note")
            .discountAmount(new BigDecimal("2.00"))
            .taxAmount(new BigDecimal("1.50"))
            .taxPercent(new BigDecimal("5.0"))
            .checkoutId("checkout123")
            .build();

        CheckoutItemVm item2 = CheckoutItemVm.builder()
            .id(2L)
            .productId(102L)
            .productName("Product 2")
            .quantity(1)
            .productPrice(new BigDecimal("9.99"))
            .note("Second item note")
            .discountAmount(new BigDecimal("1.00"))
            .taxAmount(new BigDecimal("0.75"))
            .taxPercent(new BigDecimal("5.0"))
            .checkoutId("checkout123")
            .build();

        Set<CheckoutItemVm> checkoutItemVms = new HashSet<>();
        checkoutItemVms.add(item1);
        checkoutItemVms.add(item2);

        return new CheckoutVm(
            "checkout123",
            "user@example.com",
            "Please deliver after 5 PM",
            "DISCOUNT20",
            checkoutItemVms
        );
    }
}