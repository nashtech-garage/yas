package com.yas.payment.flow.handler;

import com.yas.payment.service.PaymentSagaService;
import com.yas.payment.viewmodel.CapturedPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CapturePaymentHandler implements GenericHandler<CapturedPayment> {

    private final PaymentSagaService paymentSagaService;

    @Override
    public Object handle(CapturedPayment payload, MessageHeaders headers) {
        paymentSagaService.completePayment(payload);
        return null;
    }
}
