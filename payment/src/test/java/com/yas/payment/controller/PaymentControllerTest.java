package com.yas.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void initPayment_ShouldReturnSuccess() throws Exception {
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
            .paymentMethod("PAYPAL")
            .totalPrice(BigDecimal.TEN)
            .checkoutId("checkout123")
            .build();

        InitPaymentResponseVm response = new InitPaymentResponseVm("success", "pay123", "http://redirect.com");

        when(paymentService.initPayment(any(InitPaymentRequestVm.class))).thenReturn(response);

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void capturePayment_ShouldReturnSuccess() throws Exception {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
            .paymentMethod("PAYPAL")
            .token("token123")
            .build();

        CapturePaymentResponseVm response = CapturePaymentResponseVm.builder()
            .checkoutId("checkout123")
            .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class))).thenReturn(response);

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void cancelPayment_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/cancel"))
            .andExpect(status().isOk())
            .andExpect(content().string("Payment cancelled"));
    }
}
