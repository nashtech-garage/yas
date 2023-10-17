package com.yas.paymentpaypal.flow.handler;

import com.yas.paymentpaypal.service.PaypalService;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePaymentHandler implements GenericHandler<Double> {

    private final PaypalService paypalService;

    @Override
    public Object handle(Double payload, MessageHeaders headers) {
        paypalService.createPayment(payload);
        return null;
    }
}
