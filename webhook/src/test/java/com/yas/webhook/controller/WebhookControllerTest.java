package com.yas.webhook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = WebhookController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getPageableWebhooks_shouldReturnPage() throws Exception {
        WebhookListGetVm response = WebhookListGetVm.builder()
                .webhooks(List.of())
                .pageNo(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .isLast(true)
                .build();
        when(webhookService.getPageableWebhooks(anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/backoffice/webhooks/paging"))
                .andExpect(status().isOk());
    }

    @Test
    void listWebhooks_shouldReturnList() throws Exception {
        WebhookVm vm = new WebhookVm();
        vm.setPayloadUrl("http://localhost");
        when(webhookService.findAllWebhooks()).thenReturn(List.of(vm));

        mockMvc.perform(get("/backoffice/webhooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].payloadUrl").value("http://localhost"));
    }

    @Test
    void getWebhook_shouldReturnWebhook() throws Exception {
        WebhookDetailVm vm = new WebhookDetailVm();
        vm.setPayloadUrl("http://localhost");
        when(webhookService.findById(1L)).thenReturn(vm);

        mockMvc.perform(get("/backoffice/webhooks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payloadUrl").value("http://localhost"));
    }

    @Test
    void createWebhook_shouldReturnCreated() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm("http://localhost", "secret", "secret", true, List.of());
        WebhookDetailVm vm = new WebhookDetailVm();
        vm.setId(1L);
        when(webhookService.create(any(WebhookPostVm.class))).thenReturn(vm);

        mockMvc.perform(post("/backoffice/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateWebhook_shouldReturnNoContent() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm("http://localhost", "secret", "secret", true, List.of());
        doNothing().when(webhookService).update(any(WebhookPostVm.class), anyLong());

        mockMvc.perform(put("/backoffice/webhooks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWebhook_shouldReturnNoContent() throws Exception {
        doNothing().when(webhookService).delete(1L);

        mockMvc.perform(delete("/backoffice/webhooks/1"))
                .andExpect(status().isNoContent());
    }
}
