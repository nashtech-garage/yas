package com.yas.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentProviderController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentProviderControllerTest {

    @MockitoBean
    private PaymentProviderService paymentProviderService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void create_WhenValidRequest_ReturnsCreated() throws Exception {
        CreatePaymentVm createVm = new CreatePaymentVm();
        createVm.setId("PAYPAL");
        createVm.setName("PayPal");
        createVm.setConfigureUrl("https://paypal.com/config");
        createVm.setEnabled(true);
        PaymentProviderVm responseVm = new PaymentProviderVm("PAYPAL", "PayPal", "https://paypal.com/config", 1, 1L, null);

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(responseVm);

        mockMvc.perform(post("/backoffice/payment-providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("PAYPAL"))
                .andExpect(jsonPath("$.name").value("PayPal"));
    }

    @Test
    void update_WhenValidRequest_ReturnsOk() throws Exception {
        UpdatePaymentVm updateVm = new UpdatePaymentVm();
        updateVm.setId("PAYPAL");
        updateVm.setName("PayPal Updated");
        updateVm.setConfigureUrl("https://paypal.com/config");
        updateVm.setEnabled(true);
        PaymentProviderVm responseVm = new PaymentProviderVm("PAYPAL", "PayPal Updated", "https://paypal.com/config", 2, 1L, null);

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(responseVm);

        mockMvc.perform(put("/backoffice/payment-providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PayPal Updated"));
    }

    @Test
    void getAll_ReturnsPaymentProviders() throws Exception {
        List<PaymentProviderVm> providers = List.of(
                new PaymentProviderVm("PAYPAL", "PayPal", "https://paypal.com", 1, 1L, null)
        );
        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class)))
                .thenReturn(providers);

        mockMvc.perform(get("/storefront/payment-providers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PAYPAL"));
    }
}
