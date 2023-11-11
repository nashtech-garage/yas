package com.yas.payment.strategy;

import com.yas.payment.viewmodel.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;

public class PaypalPayment implements PaymentStrategy{
    private final MessageChannel createPaymentMessageChannel;

    public PaypalPayment(){
    }

    @Override
    public void purchase(BigDecimal totalPrice) {
        createPaymentMessageChannel.send(MessageBuilder.withPayload(totalPrice).build());
    }

    @Override
    public void refund() {

    }
}
