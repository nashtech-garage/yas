package com.yas.product.saga.handler;

import com.yas.product.service.ProductService;
import com.yas.saga.product.command.RestoreProductStockQuantityCommand;
import com.yas.saga.product.command.SubtractProductStockQuantityCommand;
import com.yas.saga.product.reply.RestoreProductStockQuantitySuccess;
import com.yas.saga.product.reply.SubtractProductStockQuantitySuccess;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@Component
@RequiredArgsConstructor
public class ProductCommandHandler {

    private final ProductService productService;

    public CommandHandlers commandHandlerDefinitions() {
        return SagaCommandHandlersBuilder
            .fromChannel("productService")
            .onMessage(SubtractProductStockQuantityCommand.class, this::subtractProductStockQuantity)
            .onMessage(RestoreProductStockQuantityCommand.class, this::restoreProductStockQuantity)
            .build();
    }

    private Message restoreProductStockQuantity(CommandMessage<RestoreProductStockQuantityCommand> restoreProductStockQuantityCommand) {
        var command = restoreProductStockQuantityCommand.getCommand();
        this.productService.restoreStockQuantity(command.productItems());
        return withSuccess(
            RestoreProductStockQuantitySuccess
                .builder()
                .message("Restore product stock quantity success")
                .build()
        );
    }

    private Message subtractProductStockQuantity(CommandMessage<SubtractProductStockQuantityCommand> subtractProductQuantityCommand) {
        var command = subtractProductQuantityCommand.getCommand();
        this.productService.subtractStockQuantity(command.productItems());
        return withSuccess(
            SubtractProductStockQuantitySuccess
                .builder()
                .message("Subtract product stock quantity success")
                .build()
        );
    }
}
