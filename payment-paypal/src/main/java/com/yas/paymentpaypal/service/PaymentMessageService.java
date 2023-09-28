package com.yas.paymentpaypal.service;

import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMessageService {

    private final MessageChannel capturePaymentMessageChanel;

    public void sendCaptureMessage(CapturedPaymentVm capturedPayment) {
        capturePaymentMessageChanel.send(MessageBuilder.withPayload(capturedPayment).build());
    }
}
