package com.yas.order.viewmodel.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.order.model.OrderItem;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderItemVmTest {

    @Test
    void fromModel_shouldMapAllFields() {
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Test Product")
                .quantity(5)
                .productPrice(new BigDecimal("29.99"))
                .note("Note")
                .discountAmount(new BigDecimal("5.00"))
                .taxAmount(new BigDecimal("2.40"))
                .taxPercent(new BigDecimal("8.00"))
                .orderId(10L)
                .build();

        OrderItemVm result = OrderItemVm.fromModel(orderItem);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100L, result.productId());
        assertEquals("Test Product", result.productName());
        assertEquals(5, result.quantity());
        assertEquals(new BigDecimal("29.99"), result.productPrice());
        assertEquals("Note", result.note());
        assertEquals(new BigDecimal("5.00"), result.discountAmount());
        assertEquals(new BigDecimal("2.40"), result.taxAmount());
        assertEquals(new BigDecimal("8.00"), result.taxPercent());
        assertEquals(10L, result.orderId());
    }

    @Test
    void builder_shouldCreateValidVm() {
        OrderItemVm vm = OrderItemVm.builder()
                .id(1L)
                .productId(100L)
                .productName("Product")
                .quantity(3)
                .productPrice(new BigDecimal("19.99"))
                .note("Test")
                .orderId(5L)
                .build();

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals(100L, vm.productId());
    }

    @Test
    void fromModel_whenFieldsAreNull_shouldMapNulls() {
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .build();

        OrderItemVm result = OrderItemVm.fromModel(orderItem);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100L, result.productId());
        assertThat(result.productName()).isNull();
        assertThat(result.note()).isNull();
    }
}
