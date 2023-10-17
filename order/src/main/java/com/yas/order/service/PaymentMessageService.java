package com.yas.order.service;

import com.yas.order.viewmodel.payment.PaymentRequest;
import com.yas.saga.payment.command.*;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

@Service
public class PaymentMessageService {
    private final String PRODUCT_SERVICE = "productService";
    public CommandWithDestination createPayment(PaymentRequest paymentRequest) {
        var paymentMethod = PaymentMethodCommand
                .valueOf(paymentRequest.paymentMethod().name());

        var createPaymentCommand = CreatePaymentCommand.builder()
                .totalPrice(paymentRequest.totalPrice())
                .paymentMethod(paymentMethod)
                .build();
        return send(createPaymentCommand)
                .to(PRODUCT_SERVICE)
                .build();
    }


}