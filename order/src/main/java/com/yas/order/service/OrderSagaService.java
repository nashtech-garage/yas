package com.yas.order.service;

import com.yas.order.saga.CreateOrderSaga;
import com.yas.order.saga.data.CreateOrderSagaData;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderSagaService {

    private final SagaInstanceFactory sagaInstanceFactory;

    private final CreateOrderSaga createOrderSaga;

    public OrderVm createOrder(OrderPostVm orderPostVm, String customerId) {

        CreateOrderSagaData data = CreateOrderSagaData.builder()
                .customerId(customerId)
                .orderPostVm(orderPostVm)
                .build();
        sagaInstanceFactory.create(createOrderSaga, data);
        return data.getOrderVm();
    }
}
