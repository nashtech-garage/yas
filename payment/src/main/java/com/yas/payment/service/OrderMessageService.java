package com.yas.payment.service;

import com.yas.payment.saga.data.PaymentData;
import com.yas.saga.order.command.CheckoutStatusCommand;
import com.yas.saga.order.command.PaymentStatusCommand;
import com.yas.saga.order.command.UpdateCheckoutStatusCommand;
import com.yas.saga.order.command.UpdateOrderPaymentStatusCommand;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import org.springframework.stereotype.Service;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

@Service
public class OrderMessageService {

    public CommandWithDestination sendUpdateCheckoutStatusCommand(PaymentData payment) {
        var command = UpdateCheckoutStatusCommand
                .builder()
                .checkoutId(payment.checkoutId())
                .checkoutStatus(CheckoutStatusCommand.COMPLETED)
                .build();
        return send(command)
                .to("orderService")
                .build();
    }

    public CommandWithDestination sendUpdateOrderPaymentStatusCommand(Long orderId, PaymentData payment) {
        var paymentStatus = PaymentStatusCommand
                .valueOf(payment.paymentStatus().name());

        var updateOrderPaymentStatusCommand = UpdateOrderPaymentStatusCommand
            .builder()
            .orderId(orderId)
            .paymentId(payment.paymentId())
            .paymentStatus(paymentStatus)
            .build();

        return send(updateOrderPaymentStatusCommand)
            .to("orderService")
            .build();
    }
}
