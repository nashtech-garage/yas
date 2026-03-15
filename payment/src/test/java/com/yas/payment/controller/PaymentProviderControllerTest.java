package com.yas.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentProviderController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentProviderService paymentProviderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreated() throws Exception {
        CreatePaymentVm request = new CreatePaymentVm();
        request.setId("test-id");
        request.setName("Test Provider");
        request.setConfigureUrl("http://test.com");

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(new PaymentProviderVm());

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void update_ShouldReturnOk() throws Exception {
        UpdatePaymentVm request = new UpdatePaymentVm();
        request.setId("test-id");
        request.setName("Updated Provider");
        request.setConfigureUrl("http://test.com");

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(new PaymentProviderVm());

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void getAll_ShouldReturnOk() throws Exception {
        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class)))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/storefront/payment-providers"))
            .andExpect(status().isOk());
    }
}
