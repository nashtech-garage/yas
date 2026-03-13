package com.yas.order.viewmodel.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.order.model.OrderItem;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderItemGetVmTest {

    @Test
    void fromModel_shouldMapAllFields() {
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Test Product")
                .quantity(3)
                .productPrice(new BigDecimal("29.99"))
                .discountAmount(new BigDecimal("5.00"))
                .taxAmount(new BigDecimal("2.00"))
                .build();

        OrderItemGetVm result = OrderItemGetVm.fromModel(orderItem);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100L, result.productId());
        assertEquals("Test Product", result.productName());
        assertEquals(3, result.quantity());
        assertEquals(new BigDecimal("29.99"), result.productPrice());
        assertEquals(new BigDecimal("5.00"), result.discountAmount());
        assertEquals(new BigDecimal("2.00"), result.taxAmount());
    }

    @Test
    void fromModels_withNullCollection_shouldReturnEmptyList() {
        List<OrderItemGetVm> result = OrderItemGetVm.fromModels(null);

        assertNotNull(result);
        assertThat(result).isEmpty();
    }

    @Test
    void fromModels_withEmptyCollection_shouldReturnEmptyList() {
        List<OrderItemGetVm> result = OrderItemGetVm.fromModels(Collections.emptySet());

        assertNotNull(result);
        assertThat(result).isEmpty();
    }

    @Test
    void fromModels_withMultipleItems_shouldMapAll() {
        OrderItem item1 = OrderItem.builder().id(1L).productId(100L).productName("A").quantity(1).build();
        OrderItem item2 = OrderItem.builder().id(2L).productId(200L).productName("B").quantity(2).build();

        List<OrderItemGetVm> result = OrderItemGetVm.fromModels(Set.of(item1, item2));

        assertNotNull(result);
        assertThat(result).hasSize(2);
    }
}
