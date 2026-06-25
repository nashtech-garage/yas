package com.yas.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentProviderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentProviderService paymentProviderService;

    @InjectMocks
    private PaymentProviderController paymentProviderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentProviderController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_whenValidRequest_returnCreated() throws Exception {
        CreatePaymentVm request = new CreatePaymentVm();
        request.setId("paypal");
        request.setName("paypal");
        request.setConfigureUrl("url");
        PaymentProviderVm response = new PaymentProviderVm("paypal", "paypal", "url", 1, null, null);

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(response);

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("paypal"))
                .andExpect(jsonPath("$.name").value("paypal"));
    }

    @Test
    void update_whenValidRequest_returnOk() throws Exception {
        UpdatePaymentVm request = new UpdatePaymentVm();
        request.setId("paypal");
        request.setName("paypal");
        request.setConfigureUrl("url");
        PaymentProviderVm response = new PaymentProviderVm("paypal", "paypal", "url", 1, null, null);

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(response);

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("paypal"))
                .andExpect(jsonPath("$.name").value("paypal"));
    }

    @Test
    void getAll_whenCalled_returnList() throws Exception {
        PaymentProviderVm response = new PaymentProviderVm("paypal", "paypal", "url", 1, null, null);

        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class))).thenReturn(List.of(response));

        mockMvc.perform(get("/storefront/payment-providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("paypal"));
    }
}
