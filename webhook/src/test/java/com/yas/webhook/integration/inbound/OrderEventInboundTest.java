package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.yas.webhook.service.OrderEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;

@ExtendWith(MockitoExtension.class)
class OrderEventInboundTest {

    @Mock
    private OrderEventService orderEventService;

    private OrderEventInbound orderEventInbound;

    @BeforeEach
    void setUp() {
        orderEventInbound = new OrderEventInbound(orderEventService);
    }

    @Test
    void onOrderEvent_ShouldCallService() {
        JsonNode jsonNode = mock(JsonNode.class);
        orderEventInbound.onOrderEvent(jsonNode);
        verify(orderEventService).onOrderEvent(jsonNode);
    }
}
