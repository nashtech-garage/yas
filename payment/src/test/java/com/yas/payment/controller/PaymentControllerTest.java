package com.yas.payment.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

@WebMvcTest(controllers = PaymentController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void initPayment_whenRequestIsValid_thenReturnInitResponse() throws Exception {
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
            .paymentMethod("PAYPAL")
            .totalPrice(BigDecimal.valueOf(25))
            .checkoutId("checkout-1")
            .build();
        given(paymentService.initPayment(request))
            .willReturn(new InitPaymentResponseVm("SUCCESS", "payment-1", "https://pay.example"));

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.paymentId").value("payment-1"));
    }

    @Test
    void capturePayment_whenRequestIsValid_thenReturnCaptureResponse() throws Exception {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
            .paymentMethod("PAYPAL")
            .token("token-1")
            .build();
        given(paymentService.capturePayment(request))
            .willReturn(CapturePaymentResponseVm.builder()
                .orderId(1L)
                .checkoutId("checkout-1")
                .amount(BigDecimal.valueOf(25))
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build());

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(1))
            .andExpect(jsonPath("$.checkoutId").value("checkout-1"))
            .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));
    }

    @Test
    void cancelPayment_thenReturnCancelledMessage() throws Exception {
        mockMvc.perform(get("/cancel"))
            .andExpect(status().isOk())
            .andExpect(content().string("Payment cancelled"));
    }
}
