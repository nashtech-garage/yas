package com.yas.order.service;

import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.saga.product.command.ProductQuantityItem;
import com.yas.saga.product.command.SubtractProductStockQuantityCommand;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import org.springframework.stereotype.Service;

import java.util.Set;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

@Service
public class ProductMessageService {

    public CommandWithDestination sendSubtractProductStockQuantityCommand(Set<OrderItemVm> orderItems) {
        var productItems = orderItems.stream()
            .map(orderItem ->
                ProductQuantityItem
                .builder()
                .productId(orderItem.productId())
                .quantity(orderItem.quantity())
                .build()
            ).toList();

        var command = SubtractProductStockQuantityCommand
            .builder()
            .productItems(productItems)
            .build();
        return send(command)
                .to("productService")
                .build();
    }
}
