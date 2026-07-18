package com.yas.webhook.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    WebhookService webhookService;

    @InjectMocks
    WebhookController webhookController;

    @Test
    void getPageableWebhooksShouldReturnServicePage() {
        WebhookListGetVm page = WebhookListGetVm.builder().pageNo(0).pageSize(10).build();
        when(webhookService.getPageableWebhooks(0, 10)).thenReturn(page);

        ResponseEntity<WebhookListGetVm> response = webhookController.getPageableWebhooks(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(page);
    }

    @Test
    void listWebhooksShouldReturnAllWebhooks() {
        List<WebhookVm> webhooks = List.of(new WebhookVm());
        when(webhookService.findAllWebhooks()).thenReturn(webhooks);

        ResponseEntity<List<WebhookVm>> response = webhookController.listWebhooks();

        assertThat(response.getBody()).isSameAs(webhooks);
    }

    @Test
    void getWebhookShouldReturnServiceResult() {
        WebhookDetailVm webhook = new WebhookDetailVm();
        when(webhookService.findById(1L)).thenReturn(webhook);

        ResponseEntity<WebhookDetailVm> response = webhookController.getWebhook(1L);

        assertThat(response.getBody()).isSameAs(webhook);
    }

    @Test
    void createWebhookShouldReturnCreatedResponse() {
        WebhookPostVm request = new WebhookPostVm();
        WebhookDetailVm webhook = new WebhookDetailVm();
        webhook.setId(1L);
        when(webhookService.create(request)).thenReturn(webhook);

        ResponseEntity<WebhookDetailVm> response = webhookController.createWebhook(
            request,
            UriComponentsBuilder.fromUri(URI.create("http://localhost"))
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(webhook);
    }

    @Test
    void updateWebhookShouldReturnNoContent() {
        WebhookPostVm request = new WebhookPostVm();

        ResponseEntity<Void> response = webhookController.updateWebhook(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).update(request, 1L);
    }

    @Test
    void deleteWebhookShouldReturnNoContent() {
        ResponseEntity<Void> response = webhookController.deleteWebhook(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).delete(1L);
    }
}
