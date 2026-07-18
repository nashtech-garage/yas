package com.yas.payment.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

@WebMvcTest(controllers = PaymentProviderController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentProviderControllerTest {

    @MockitoBean
    private PaymentProviderService paymentProviderService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void create_whenRequestIsValid_thenReturnCreatedProvider() throws Exception {
        CreatePaymentVm request = createPaymentVm();
        given(paymentProviderService.create(any(CreatePaymentVm.class)))
            .willReturn(new PaymentProviderVm("paypal", "PayPal", "/configure", 1, 10L, "/icon.png"));

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("paypal"))
            .andExpect(jsonPath("$.name").value("PayPal"));
    }

    @Test
    void update_whenRequestIsValid_thenReturnUpdatedProvider() throws Exception {
        UpdatePaymentVm request = updatePaymentVm();
        given(paymentProviderService.update(any(UpdatePaymentVm.class)))
            .willReturn(new PaymentProviderVm("paypal", "PayPal Updated", "/configure", 2, 10L, "/icon.png"));

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("paypal"))
            .andExpect(jsonPath("$.name").value("PayPal Updated"));
    }

    @Test
    void getAll_whenProvidersExist_thenReturnEnabledProviders() throws Exception {
        given(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class)))
            .willReturn(List.of(new PaymentProviderVm("paypal", "PayPal", "/configure", 1, 10L, "/icon.png")));

        mockMvc.perform(get("/storefront/payment-providers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value("paypal"));
    }

    private static CreatePaymentVm createPaymentVm() {
        CreatePaymentVm request = new CreatePaymentVm();
        request.setId("paypal");
        request.setName("PayPal");
        request.setConfigureUrl("/configure");
        request.setEnabled(true);
        request.setMediaId(10L);
        return request;
    }

    private static UpdatePaymentVm updatePaymentVm() {
        UpdatePaymentVm request = new UpdatePaymentVm();
        request.setId("paypal");
        request.setName("PayPal Updated");
        request.setConfigureUrl("/configure");
        request.setEnabled(true);
        request.setMediaId(10L);
        return request;
    }
}
