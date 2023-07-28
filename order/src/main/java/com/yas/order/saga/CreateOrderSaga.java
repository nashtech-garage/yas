package com.yas.order.saga;

import com.yas.order.saga.data.CreateOrderSagaData;
import com.yas.order.service.CartService;
import com.yas.order.service.OrderService;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.saga.cart.reply.DeleteCartItemFailure;
import com.yas.saga.cart.reply.DeleteCartItemSuccess;
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
    private final CartService cartService;

    private final SagaDefinition<CreateOrderSagaData> sagaDefinition =
            step()
                .invokeLocal(this::createOrder)
                .withCompensation(this::compensateOrder)
            .step()
                .invokeParticipant(this::deleteCartItem)
                .onReply(DeleteCartItemSuccess.class, (data, reply) -> log.info(reply.getMessage()))
                .onReply(DeleteCartItemFailure.class, (data, reply) -> log.warn(reply.getMessage()))
            .build();

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
        return this.cartService.deleteCartItem(productIds, data.getCustomerId());
    }

    private void compensateOrder(CreateOrderSagaData data) {
        log.info("Compensate Order");
    }
}
