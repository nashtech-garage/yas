package com.yas.webhook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private WebhookController webhookController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getPageableWebhooks_ShouldReturnOk() throws Exception {
        when(webhookService.getPageableWebhooks(0, 10)).thenReturn(WebhookListGetVm.builder()
            .webhooks(List.of())
            .pageNo(0)
            .pageSize(10)
            .totalElements(0L)
            .totalPages(0L)
            .isLast(true)
            .build());

        mockMvc.perform(get("/backoffice/webhooks/paging?pageNo=0&pageSize=10"))
            .andExpect(status().isOk());
    }

    @Test
    void listWebhooks_ShouldReturnOk() throws Exception {
        when(webhookService.findAllWebhooks()).thenReturn(List.of());

        mockMvc.perform(get("/backoffice/webhooks"))
            .andExpect(status().isOk());
    }

    @Test
    void getWebhook_ShouldReturnOk() throws Exception {
        when(webhookService.findById(1L)).thenReturn(new WebhookDetailVm());

        mockMvc.perform(get("/backoffice/webhooks/1"))
            .andExpect(status().isOk());
    }

    @Test
    void createWebhook_ShouldReturnCreated() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm("http://test.com", "secret", "json", true, List.of());
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);
        when(webhookService.create(any(WebhookPostVm.class))).thenReturn(detailVm);

        mockMvc.perform(post("/backoffice/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isCreated());
    }

    @Test
    void updateWebhook_ShouldReturnNoContent() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm("http://test.com", "secret", "json", true, List.of());
        doNothing().when(webhookService).update(any(WebhookPostVm.class), eq(1L));

        mockMvc.perform(put("/backoffice/webhooks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteWebhook_ShouldReturnNoContent() throws Exception {
        doNothing().when(webhookService).delete(1L);

        mockMvc.perform(delete("/backoffice/webhooks/1"))
            .andExpect(status().isNoContent());
    }
}
