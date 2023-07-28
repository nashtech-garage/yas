package com.yas.order.saga.data;

import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderSagaData {
    private OrderPostVm orderPostVm;
    private OrderVm orderVm;
    private String customerId;
}
