package com.yas.payment.controller;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentProviderController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentProviderService paymentProviderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreated() throws Exception {
        CreatePaymentVm request = new CreatePaymentVm();
        request.setId("pay1");
        request.setName("Pay 1");
        request.setConfigureUrl("http://configure");

        PaymentProviderVm response = new PaymentProviderVm("pay1", "Pay 1", "http://configure", 0, null, null);

        when(paymentProviderService.create(any())).thenReturn(response);

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void update_ShouldReturnOk() throws Exception {
        UpdatePaymentVm request = new UpdatePaymentVm();
        request.setId("pay1");
        request.setName("Pay 1 Updated");
        request.setConfigureUrl("http://configure-updated");

        PaymentProviderVm response = new PaymentProviderVm("pay1", "Pay 1 Updated", "http://configure-updated", 0, null, null);

        when(paymentProviderService.update(any())).thenReturn(response);

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        PaymentProviderVm response = new PaymentProviderVm("pay1", "Pay 1", null, 0, null, null);

        when(paymentProviderService.getEnabledPaymentProviders(any())).thenReturn(List.of(response));

        mockMvc.perform(get("/storefront/payment-providers"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }
}
