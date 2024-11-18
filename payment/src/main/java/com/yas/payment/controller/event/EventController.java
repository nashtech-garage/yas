package com.yas.payment.controller.event;

import com.yas.payment.paypal.model.PaypalWebhookEvent;
import com.yas.payment.service.order.handler.base.IPaypalWebhookHandlerRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    private final IPaypalWebhookHandlerRegistry<PaypalWebhookEvent> registry;

    @PostMapping("/webhook/paypal/payment-events")
    public ResponseEntity<String> handlePaypalPaymentEvent(@RequestBody @Valid PaypalWebhookEvent payload) {

        LOGGER.info("Received webhook from PayPal: {}", payload);

        registry.get(payload.getEventType().getStatus()).handle(payload);

        return ResponseEntity.ok("Paypal webhook handled successfully");
    }
}
