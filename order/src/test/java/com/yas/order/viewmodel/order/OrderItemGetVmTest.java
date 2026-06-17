package com.yas.order.viewmodel.order;

import com.yas.order.model.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemGetVmTest {

    @Test
    void fromModelShouldMapOrderItemToOrderItemGetVm() {
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Test Product")
                .quantity(2)
                .productPrice(BigDecimal.valueOf(10.50))
                .discountAmount(BigDecimal.valueOf(1.50))
                .taxAmount(BigDecimal.valueOf(0.75))
                .build();

        OrderItemGetVm orderItemGetVm = OrderItemGetVm.fromModel(orderItem);

        assertEquals(1L, orderItemGetVm.id());
        assertEquals(100L, orderItemGetVm.productId());
        assertEquals("Test Product", orderItemGetVm.productName());
        assertEquals(2, orderItemGetVm.quantity());
        assertEquals(BigDecimal.valueOf(10.50), orderItemGetVm.productPrice());
        assertEquals(BigDecimal.valueOf(1.50), orderItemGetVm.discountAmount());
        assertEquals(BigDecimal.valueOf(0.75), orderItemGetVm.taxAmount());
    }

    @Test
    void fromModelsShouldMapOrderItemsToOrderItemGetVms() {
        OrderItem first = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .productName("First Product")
                .quantity(2)
                .productPrice(BigDecimal.valueOf(10.50))
                .discountAmount(BigDecimal.valueOf(1.50))
                .taxAmount(BigDecimal.valueOf(0.75))
                .build();
        OrderItem second = OrderItem.builder()
                .id(2L)
                .productId(200L)
                .productName("Second Product")
                .quantity(3)
                .productPrice(BigDecimal.valueOf(20.50))
                .discountAmount(BigDecimal.valueOf(2.50))
                .taxAmount(BigDecimal.valueOf(1.25))
                .build();

        List<OrderItemGetVm> orderItemGetVms = OrderItemGetVm.fromModels(List.of(first, second));

        assertEquals(2, orderItemGetVms.size());
        assertEquals(OrderItemGetVm.fromModel(first), orderItemGetVms.get(0));
        assertEquals(OrderItemGetVm.fromModel(second), orderItemGetVms.get(1));
    }
}