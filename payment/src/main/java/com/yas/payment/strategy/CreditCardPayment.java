package com.yas.payment.strategy;

import com.yas.payment.viewmodel.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public final class CreditCardPayment implements PaymentStrategy{
    private final MessageChannel createPaymentMessageChannel;

    @Override
    public void purchase(BigDecimal totalPrice) {
        createPaymentMessageChannel.send(MessageBuilder.withPayload(totalPrice).build());
    }

    @Override
    public void refund() {

    }
}
