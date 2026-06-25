package com.yas.webhook.integration.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import tools.jackson.databind.JsonNode;

@ExtendWith(MockitoExtension.class)
class WebhookApiTest {

    @Mock
    private RestClient restClient;

    private WebhookApi webhookApi;

    @BeforeEach
    void setUp() {
        webhookApi = new WebhookApi(restClient);
    }

    @Test
    void notify_WithSecret_ShouldCallRestClientWithHeader() {
        String url = "http://test.com";
        String secret = "secret";
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.toString()).thenReturn("{}");

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec requestBodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        webhookApi.notify(url, secret, jsonNode);

        verify(requestBodySpec).header(eq(WebhookApi.X_HUB_SIGNATURE_256), anyString());
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void notify_WithoutSecret_ShouldCallRestClientWithoutHeader() {
        String url = "http://test.com";
        String secret = "";
        JsonNode jsonNode = mock(JsonNode.class);

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec requestBodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        webhookApi.notify(url, secret, jsonNode);

        verify(responseSpec).toBodilessEntity();
    }
}
