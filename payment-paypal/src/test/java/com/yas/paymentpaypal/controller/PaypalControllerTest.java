package com.yas.paymentpaypal.controller;


import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.paymentpaypal.PaymentPaypalApplication;
import com.yas.paymentpaypal.service.PaypalService;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.RequestPayment;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaypalController.class)
@ContextConfiguration(classes = PaymentPaypalApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class PaypalControllerTest {

    @MockBean
    private PaypalService paypalService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreatePayment_whenNormalCase_responsePaypalRequestPayment() throws Exception {

        RequestPayment requestPayment = new RequestPayment(new BigDecimal(10), "1");
        PaypalRequestPayment paypalRequestPayment = new PaypalRequestPayment(
            "success",
            "PAYID-LJ4Z8A8P2R48",
            "https://www.example.com/redirect"
        );
        when(paypalService.createPayment(requestPayment)).thenReturn(paypalRequestPayment);

        mockMvc.perform(MockMvcRequestBuilders.post("/init")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(requestPayment)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(paypalRequestPayment)));

    }

    @Test
    void testCapturePayment_whenNormalCase_responseCapturedPaymentVm() throws Exception {

        String token = "testToken";
        CapturedPaymentVm payment = new CapturedPaymentVm(
            12345L,
            "chk_7890",
            new BigDecimal("250.75"),
            new BigDecimal("5.00"),
            "txn_0011223344",
            "credit_card",
            "completed",
            null
        );
        when(paypalService.capturePayment(token)).thenReturn(payment);
        mockMvc.perform(MockMvcRequestBuilders.get("/capture")
                .param("token", token)
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(payment)));

    }

    @Test
    void testCancelPayment_whenNormalCase_responseString() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cancel")
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Payment cancelled"));
    }

 }