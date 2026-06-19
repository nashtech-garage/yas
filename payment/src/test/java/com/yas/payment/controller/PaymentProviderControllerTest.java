package com.yas.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaymentProviderController.class, excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentProviderService paymentProviderService;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void create_shouldReturnCreatedPaymentProvider() throws Exception {
        CreatePaymentVm request = new CreatePaymentVm();
        request.setId("PAYPAL");
        request.setName("PAYPAL");
        request.setConfigureUrl("paypal config");
        
        PaymentProviderVm response = new PaymentProviderVm("paypalId", "PAYPAL", "paypal config", 1, 1L, "icon");

        given(paymentProviderService.create(any(CreatePaymentVm.class))).willReturn(response);

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("paypalId"))
            .andExpect(jsonPath("$.name").value("PAYPAL"));
    }

    @Test
    void update_shouldReturnUpdatedPaymentProvider() throws Exception {
        UpdatePaymentVm request = new UpdatePaymentVm();
        request.setId("PAYPAL");
        request.setName("PAYPAL");
        request.setConfigureUrl("paypal config 2");
        
        PaymentProviderVm response = new PaymentProviderVm("paypalId", "PAYPAL", "paypal config 2", 1, 1L, "icon");

        given(paymentProviderService.update(any(UpdatePaymentVm.class))).willReturn(response);

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("paypalId"));
    }

    @Test
    void getAll_shouldReturnEnabledPaymentProviders() throws Exception {
        PaymentProviderVm response = new PaymentProviderVm("paypalId", "PAYPAL", "paypal config", 1, 1L, "icon");
        given(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class))).willReturn(List.of(response));

        mockMvc.perform(get("/storefront/payment-providers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("paypalId"))
            .andExpect(jsonPath("$[0].name").value("PAYPAL"));
    }
}
