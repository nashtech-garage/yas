package com.yas.order.viewmodel.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderGetVmTest {

    @Test
    void fromModel_withOrderItems_shouldMapCorrectly() {
        Order order = Order.builder()
                .id(1L)
                .orderStatus(OrderStatus.COMPLETED)
                .totalPrice(new BigDecimal("100.00"))
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .build();

        Set<OrderItem> items = new HashSet<>();
        items.add(OrderItem.builder()
                .id(1L).productId(100L).productName("Product").quantity(2)
                .productPrice(new BigDecimal("50.00")).build());

        OrderGetVm result = OrderGetVm.fromModel(order, items);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(OrderStatus.COMPLETED, result.orderStatus());
        assertEquals(new BigDecimal("100.00"), result.totalPrice());
        assertEquals(DeliveryStatus.DELIVERED, result.deliveryStatus());
        assertEquals(DeliveryMethod.YAS_EXPRESS, result.deliveryMethod());
        assertThat(result.orderItems()).hasSize(1);
    }

    @Test
    void fromModel_withNullItems_shouldReturnEmptyList() {
        Order order = Order.builder()
                .id(1L)
                .orderStatus(OrderStatus.PENDING)
                .build();

        OrderGetVm result = OrderGetVm.fromModel(order, null);

        assertNotNull(result);
        assertThat(result.orderItems()).isEmpty();
    }

    @Test
    void fromModel_withEmptyItems_shouldReturnEmptyList() {
        Order order = Order.builder()
                .id(1L)
                .orderStatus(OrderStatus.PENDING)
                .build();

        OrderGetVm result = OrderGetVm.fromModel(order, Collections.emptySet());

        assertNotNull(result);
        assertThat(result.orderItems()).isEmpty();
    }
}
