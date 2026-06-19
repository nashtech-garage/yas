package com.yas.webhook.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    WebhookService webhookService;

    @InjectMocks
    WebhookController webhookController;

    @Test
    void getPageableWebhooks_ReturnsWebhookList() {
        WebhookListGetVm expected = org.mockito.Mockito.mock(WebhookListGetVm.class);
        when(webhookService.getPageableWebhooks(0, 10)).thenReturn(expected);

        ResponseEntity<WebhookListGetVm> response = webhookController.getPageableWebhooks(0, 10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void listWebhooks_ReturnsList() {
        List<WebhookVm> expected = List.of(new WebhookVm());
        when(webhookService.findAllWebhooks()).thenReturn(expected);

        ResponseEntity<List<WebhookVm>> response = webhookController.listWebhooks();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getWebhook_ReturnsDetailVm() {
        WebhookDetailVm expected = new WebhookDetailVm();
        when(webhookService.findById(1L)).thenReturn(expected);

        ResponseEntity<WebhookDetailVm> response = webhookController.getWebhook(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void createWebhook_ReturnsCreated() {
        WebhookPostVm postVm = new WebhookPostVm();
        WebhookDetailVm expected = new WebhookDetailVm();
        expected.setId(1L);
        when(webhookService.create(postVm)).thenReturn(expected);

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        ResponseEntity<WebhookDetailVm> response = webhookController.createWebhook(postVm, builder);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void updateWebhook_ReturnsNoContent() {
        WebhookPostVm postVm = new WebhookPostVm();

        ResponseEntity<Void> response = webhookController.updateWebhook(1L, postVm);

        assertEquals(204, response.getStatusCode().value());
        verify(webhookService).update(postVm, 1L);
    }

    @Test
    void deleteWebhook_ReturnsNoContent() {
        ResponseEntity<Void> response = webhookController.deleteWebhook(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(webhookService).delete(1L);
    }
}
