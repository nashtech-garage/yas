package com.yas.payment.controller;

import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import com.yas.payment.model.enumeration.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void initPayment_ShouldReturnResponse() throws Exception {
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(BigDecimal.TEN)
                .checkoutId("checkout1")
                .build();
        
        InitPaymentResponseVm response = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("pay123")
                .redirectUrl("http://redirect")
                .build();

        when(paymentService.initPayment(any())).thenReturn(response);

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void capturePayment_ShouldReturnResponse() throws Exception {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("token123")
                .build();

        CapturePaymentResponseVm response = CapturePaymentResponseVm.builder()
                .orderId(1L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        when(paymentService.capturePayment(any())).thenReturn(response);

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void cancelPayment_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment cancelled"));
    }
}
