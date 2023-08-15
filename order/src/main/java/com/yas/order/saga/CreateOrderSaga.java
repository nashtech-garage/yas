package com.yas.order.saga;

import com.yas.order.saga.data.CreateOrderSagaData;
import com.yas.order.service.CartMessageService;
import com.yas.order.service.OrderService;
import com.yas.order.service.ProductMessageService;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.saga.cart.reply.DeleteCartItemFailure;
import com.yas.saga.cart.reply.DeleteCartItemSuccess;
import com.yas.saga.product.reply.SubtractProductStockQuantitySuccess;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {

    private final OrderService orderService;
    private final CartMessageService cartMessageService;
    private final ProductMessageService productMessageService;

    private final SagaDefinition<CreateOrderSagaData> sagaDefinition =
            step()
                .invokeLocal(this::createOrder)
                .withCompensation(this::compensateOrder)
            .step()
                .invokeParticipant(this::deleteCartItem)
                .onReply(DeleteCartItemSuccess.class, (data, reply) -> log.info(reply.getMessage()))
                .onReply(DeleteCartItemFailure.class, (data, reply) -> log.warn(reply.getMessage()))
            .step()
                .invokeParticipant(this::subtractProductStockQuantity)
                .onReply(SubtractProductStockQuantitySuccess.class, (data, reply) -> log.info(reply.message()))
            .build();

    private CommandWithDestination subtractProductStockQuantity(CreateOrderSagaData data) {
        return this.productMessageService.sendSubtractProductStockQuantityCommand(data.getOrderVm().orderItemVms());
    }

    @Override
    public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
        return this.sagaDefinition;
    }

    private void createOrder(CreateOrderSagaData data) {
        OrderVm orderVm = this.orderService.createOrder(data.getOrderPostVm());
        data.setOrderVm(orderVm);
    }

    private CommandWithDestination deleteCartItem(CreateOrderSagaData data) {
        OrderPostVm orderPostVm = data.getOrderPostVm();
        List<Long> productIds = orderPostVm.orderItemPostVms()
                .stream()
                .map(OrderItemPostVm::productId)
                .toList();
        return this.cartMessageService.deleteCartItem(productIds, data.getCustomerId());
    }

    private void compensateOrder(CreateOrderSagaData data) {
        log.info("Compensate Order");
    }
}
