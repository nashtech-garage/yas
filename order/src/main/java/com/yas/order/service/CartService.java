package com.yas.order.service;

import com.yas.saga.cart.command.DeleteCartItemCommand;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

@Service
public class CartService {
    public CommandWithDestination deleteCartItem(List<Long> productIds, String customerId) {
        var deleteCartItemCommand = DeleteCartItemCommand.builder()
                .customerId(customerId)
                .productIds(productIds)
                .build();
        return send(deleteCartItemCommand)
                .to("cartService")
                .build();
    }
}