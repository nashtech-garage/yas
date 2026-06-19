package com.yas.delivery.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.yas.delivery.service.DeliveryService;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService deliveryService;

    @Test
    void testGetStatus() throws Exception {
        String statusMessage = "Delivery Service is up and running";
        when(deliveryService.getStatus()).thenReturn(statusMessage);

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string(statusMessage));
    }
}
