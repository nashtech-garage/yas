package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.yas.webhook.service.ProductEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;

@ExtendWith(MockitoExtension.class)
class ProductEventInboundTest {

    @Mock
    private ProductEventService productEventService;

    private ProductEventInbound productEventInbound;

    @BeforeEach
    void setUp() {
        productEventInbound = new ProductEventInbound(productEventService);
    }

    @Test
    void onProductEvent_ShouldCallService() {
        JsonNode jsonNode = mock(JsonNode.class);
        productEventInbound.onProductEvent(jsonNode);
        verify(productEventService).onProductEvent(jsonNode);
    }
}
