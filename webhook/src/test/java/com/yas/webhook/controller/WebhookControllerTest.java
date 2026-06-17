package com.yas.webhook.controller;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebhookControllerTest {

    private MockMvc mockMvc;
    private WebhookService webhookService;

    @BeforeEach
    void setUp() {
        webhookService = Mockito.mock(WebhookService.class);
        WebhookController webhookController = new WebhookController(webhookService);
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build();
    }

    @Test
    void getPageableWebhooks_shouldReturnOk() throws Exception {
        WebhookListGetVm response = WebhookListGetVm.builder()
                .webhooks(Collections.emptyList())
                .pageNo(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .isLast(false)
                .build();
        when(webhookService.getPageableWebhooks(anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/backoffice/webhooks/paging")
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void listWebhooks_shouldReturnOk() throws Exception {
        List<WebhookVm> response = Collections.emptyList();
        when(webhookService.findAllWebhooks()).thenReturn(response);

        mockMvc.perform(get("/backoffice/webhooks"))
                .andExpect(status().isOk());
    }

    @Test
    void getWebhook_shouldReturnOk() throws Exception {
        WebhookDetailVm response = new WebhookDetailVm();
        response.setId(1L);
        when(webhookService.findById(anyLong())).thenReturn(response);

        mockMvc.perform(get("/backoffice/webhooks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createWebhook_shouldReturnCreated() throws Exception {
        WebhookDetailVm response = new WebhookDetailVm();
        response.setId(1L);
        when(webhookService.create(any(WebhookPostVm.class))).thenReturn(response);

        mockMvc.perform(post("/backoffice/webhooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payloadUrl\":\"http://test.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateWebhook_shouldReturnNoContent() throws Exception {
        doNothing().when(webhookService).update(any(WebhookPostVm.class), anyLong());

        mockMvc.perform(put("/backoffice/webhooks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payloadUrl\":\"http://test.com\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWebhook_shouldReturnNoContent() throws Exception {
        doNothing().when(webhookService).delete(anyLong());

        mockMvc.perform(delete("/backoffice/webhooks/1"))
                .andExpect(status().isNoContent());
    }
}
