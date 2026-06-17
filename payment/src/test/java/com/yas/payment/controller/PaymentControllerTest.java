package com.yas.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaymentController.class, excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void initPayment_shouldReturnInitPaymentResponseVm() throws Exception {
        InitPaymentRequestVm request = new InitPaymentRequestVm("PAYPAL", BigDecimal.TEN, "checkout123");
        InitPaymentResponseVm response = new InitPaymentResponseVm("CREATED", "paymentId123", "http://paypal.url");

        given(paymentService.initPayment(any(InitPaymentRequestVm.class))).willReturn(response);

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.redirectUrl").value("http://paypal.url"));
    }

    @Test
    void capturePayment_shouldReturnCapturePaymentResponseVm() throws Exception {
        CapturePaymentRequestVm request = new CapturePaymentRequestVm("PAYPAL", "token123");
        CapturePaymentResponseVm response = new CapturePaymentResponseVm(
                1L, "checkout123", BigDecimal.TEN, BigDecimal.ONE,
                "tx123", PaymentMethod.PAYPAL, PaymentStatus.COMPLETED, null);

        given(paymentService.capturePayment(any(CapturePaymentRequestVm.class))).willReturn(response);

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));
    }

    @Test
    void cancelPayment_shouldReturnStringMessage() throws Exception {
        mockMvc.perform(get("/cancel"))
            .andExpect(status().isOk())
            .andExpect(content().string("Payment cancelled"));
    }
}
