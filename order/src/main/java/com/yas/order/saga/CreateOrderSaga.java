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
import com.yas.saga.product.reply.RestoreProductStockQuantitySuccess;
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
                .withCompensation(this::rejectOrder)
            .step()
                .invokeParticipant(this::subtractProductStockQuantity)
                .withCompensation(this::restoreProductStockQuantity)
                .onReply(SubtractProductStockQuantitySuccess.class, (data, reply) -> log.info(reply.message()))
                .onReply(RestoreProductStockQuantitySuccess.class, (data, reply) -> log.info(reply.message()))
            .step()
                .invokeParticipant(this::deleteCartItem)
                .onReply(DeleteCartItemSuccess.class, (data, reply) -> log.info(reply.getMessage()))
                .onReply(DeleteCartItemFailure.class, this::onDeleteCartItemFailure)
            .step()
                .invokeLocal(this::acceptOrder)
            .build();

    private void acceptOrder(CreateOrderSagaData data) {
        log.info("Accept Order");
        this.orderService.acceptOrder(data.getOrderVm().id());
    }

    private CommandWithDestination restoreProductStockQuantity(CreateOrderSagaData data) {
        log.warn("Restore product stock quantity");
        return this.productMessageService.sendRestoreProductStockQuantity(data.getOrderVm().orderItemVms());
    }

    private void onDeleteCartItemFailure(CreateOrderSagaData data, DeleteCartItemFailure deleteCartItemFailure) {
        log.warn("Delete cart item step is failure {}", deleteCartItemFailure.getMessage());
        data.setRejectReason(deleteCartItemFailure.getMessage());
    }

    private CommandWithDestination subtractProductStockQuantity(CreateOrderSagaData data) {
        log.info("Subtract product stock quantity");
        return this.productMessageService.sendSubtractProductStockQuantityCommand(data.getOrderVm().orderItemVms());
    }

    @Override
    public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
        return this.sagaDefinition;
    }

    private void createOrder(CreateOrderSagaData data) {
        log.info("Begin create order saga steps");
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

    private void rejectOrder(CreateOrderSagaData data) {
        log.warn("Reject order with reason: {}", data.getRejectReason());
        this.orderService.rejectOrder(data.getOrderVm().id(), data.getRejectReason());
    }
}
