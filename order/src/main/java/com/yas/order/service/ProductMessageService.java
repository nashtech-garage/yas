package com.yas.order.service;

import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.saga.product.command.ProductQuantityItem;
import com.yas.saga.product.command.RestoreProductStockQuantityCommand;
import com.yas.saga.product.command.SubtractProductStockQuantityCommand;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

@Service
public class ProductMessageService {

    private final String PRODUCT_SERVICE = "productService";

    public CommandWithDestination sendSubtractProductStockQuantityCommand(Set<OrderItemVm> orderItems) {
        var productItems = buildProductQuantityItems(orderItems);

        var command = SubtractProductStockQuantityCommand
            .builder()
            .productItems(productItems)
            .build();
        return send(command)
                .to(PRODUCT_SERVICE)
                .build();
    }

    private List<ProductQuantityItem> buildProductQuantityItems(Set<OrderItemVm> orderItems) {
        return orderItems.stream()
            .map(orderItem ->
                ProductQuantityItem
                .builder()
                .productId(orderItem.productId())
                .quantity(orderItem.quantity())
                .build()
            ).toList();
    }

    public CommandWithDestination sendRestoreProductStockQuantity(Set<OrderItemVm> orderItems) {
        var productItems = this.buildProductQuantityItems(orderItems);
        var command = RestoreProductStockQuantityCommand
                .builder()
                .productItems(productItems)
                .build();
        return send(command)
                .to(PRODUCT_SERVICE)
                .build();
    }
}
