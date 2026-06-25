package com.yas.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void initPayment_whenValidRequest_returnInitPaymentResponse() throws Exception {
        InitPaymentRequestVm request = new InitPaymentRequestVm("PAYPAL", BigDecimal.TEN, "checkout123");
        InitPaymentResponseVm response = InitPaymentResponseVm.builder()
                .paymentId("pay123")
                .redirectUrl("http://redirect.url")
                .status(PaymentStatus.PENDING.name())
                .build();

        when(paymentService.initPayment(any(InitPaymentRequestVm.class))).thenReturn(response);

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("pay123"))
                .andExpect(jsonPath("$.redirectUrl").value("http://redirect.url"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void capturePayment_whenValidRequest_returnCapturePaymentResponse() throws Exception {
        CapturePaymentRequestVm request = new CapturePaymentRequestVm("PAYPAL", "token123");
        CapturePaymentResponseVm response = CapturePaymentResponseVm.builder()
                .orderId(1L)
                .checkoutId("checkout123")
                .amount(BigDecimal.TEN)
                .paymentFee(BigDecimal.ONE)
                .gatewayTransactionId("txn123")
                .paymentMethod(com.yas.payment.model.enumeration.PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class))).thenReturn(response);

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.checkoutId").value("checkout123"))
                .andExpect(jsonPath("$.gatewayTransactionId").value("txn123"));
    }

    @Test
    void cancelPayment_whenCalled_returnSuccessMessage() throws Exception {
        mockMvc.perform(get("/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment cancelled"));
    }
}
