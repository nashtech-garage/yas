package com.yas.cart.saga.handler;

import com.yas.cart.exception.BadRequestException;
import com.yas.cart.exception.NotFoundException;
import com.yas.cart.service.CartService;
import com.yas.saga.cart.command.DeleteCartItemCommand;
import com.yas.saga.cart.reply.DeleteCartItemFailure;
import com.yas.saga.cart.reply.DeleteCartItemSuccess;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@Component
@RequiredArgsConstructor
public class CartItemCommandHandler {

    private final CartService cartService;

    public CommandHandlers commandHandlerDefinitions() {
        return SagaCommandHandlersBuilder
                .fromChannel("cartService")
                .onMessage(DeleteCartItemCommand.class, this::deleteCartItem)
                .build();
    }

    private Message deleteCartItem(CommandMessage<DeleteCartItemCommand> cm) {
        DeleteCartItemCommand cmd = cm.getCommand();
        try {
            this.cartService.removeCartItemListByProductIdList(cmd.getProductIds(), cmd.getCustomerId());
            String productIdsStr = StringUtils.join(cmd.getProductIds(), ",");
            return withSuccess(new DeleteCartItemSuccess("Delete all card items of product ids [" + productIdsStr + "] success"));
        } catch (BadRequestException | NotFoundException e) {
            return withFailure(new DeleteCartItemFailure(e.getMessage()));
        }
    }
}
