package com.yas.order.saga.handler;

import com.yas.order.exception.NotFoundException;
import com.yas.order.service.CheckoutService;
import com.yas.order.service.OrderService;
import com.yas.saga.order.command.UpdateCheckoutStatusCommand;
import com.yas.saga.order.command.UpdateOrderPaymentStatusCommand;
import com.yas.saga.order.reply.CheckoutNotFound;
import com.yas.saga.order.reply.OrderNotFound;
import com.yas.saga.order.reply.UpdateCheckoutStatusSuccess;
import com.yas.saga.order.reply.UpdateOrderPaymentStatusSuccess;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@Component
@RequiredArgsConstructor
public class OrderCommandHandler {

    private final OrderService orderService;
    private final CheckoutService checkoutService;

    public CommandHandlers commandHandlerDefinitions() {
        return SagaCommandHandlersBuilder
            .fromChannel("orderService")
            .onMessage(UpdateCheckoutStatusCommand.class, this::updateCheckoutStatus)
            .onMessage(UpdateOrderPaymentStatusCommand.class, this::updateOrderPaymentStatus)
            .build();
    }

    private Message updateCheckoutStatus(CommandMessage<UpdateCheckoutStatusCommand> updateCheckoutStatusCommand) {
        UpdateCheckoutStatusCommand command = updateCheckoutStatusCommand.getCommand();
        try {
            var checkout = this.checkoutService.updateCheckoutStatus(command);

            var order = this.orderService.findOrderByCheckoutId(checkout.getId());
            return withSuccess(
                UpdateCheckoutStatusSuccess
                    .builder()
                    .orderId(order.getId())
                    .build()
            );
        } catch (NotFoundException ex) {
            return withFailure(
                CheckoutNotFound
                    .builder()
                    .message(ex.getMessage())
                    .build()
            );
        }
    }

    private Message updateOrderPaymentStatus(CommandMessage<UpdateOrderPaymentStatusCommand> commandMessage) {
        var command = commandMessage.getCommand();
        try {
            var order = this.orderService.updateOrderPaymentStatus(command);

            return withSuccess(
                UpdateOrderPaymentStatusSuccess
                    .builder()
                    .orderId(order.getId())
                    .orderStatus(order.getOrderStatus().name())
                    .build()
            );
        } catch (NotFoundException ex) {
            return withFailure(
                OrderNotFound
                    .builder()
                    .message(ex.getMessage())
                    .build()
            );
        }
    }
}
