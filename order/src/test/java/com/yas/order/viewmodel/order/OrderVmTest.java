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
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderVmTest {

    @Test
    void fromModel_whenValidOrderWithItems_shouldMapAllFields() {
        OrderAddress shipping = OrderAddress.builder()
                .id(1L).contactName("Ship Contact").phone("111").addressLine1("Ship Addr")
                .city("City1").zipCode("11111").districtId(1L).districtName("D1")
                .stateOrProvinceId(1L).stateOrProvinceName("S1").countryId(1L).countryName("C1")
                .build();
        OrderAddress billing = OrderAddress.builder()
                .id(2L).contactName("Bill Contact").phone("222").addressLine1("Bill Addr")
                .city("City2").zipCode("22222").districtId(2L).districtName("D2")
                .stateOrProvinceId(2L).stateOrProvinceName("S2").countryId(2L).countryName("C2")
                .build();

        Order order = Order.builder()
                .id(1L)
                .email("test@test.com")
                .note("note")
                .tax(5.0f)
                .discount(10.0f)
                .numberItem(2)
                .totalPrice(new BigDecimal("100.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("CODE")
                .orderStatus(OrderStatus.COMPLETED)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .paymentStatus(PaymentStatus.COMPLETED)
                .shippingAddressId(shipping)
                .billingAddressId(billing)
                .checkoutId("checkout-1")
                .build();

        Set<OrderItem> items = new HashSet<>();
        items.add(OrderItem.builder()
                .id(1L).productId(100L).productName("Product").quantity(2)
                .productPrice(new BigDecimal("50.00")).orderId(1L).build());

        OrderVm result = OrderVm.fromModel(order, items);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("test@test.com", result.email());
        assertNotNull(result.shippingAddressVm());
        assertNotNull(result.billingAddressVm());
        assertEquals("note", result.note());
        assertEquals(5.0f, result.tax());
        assertEquals(10.0f, result.discount());
        assertEquals(2, result.numberItem());
        assertEquals(new BigDecimal("100.00"), result.totalPrice());
        assertEquals("CODE", result.couponCode());
        assertEquals(OrderStatus.COMPLETED, result.orderStatus());
        assertEquals("checkout-1", result.checkoutId());
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void fromModel_whenOrderItemsIsNull_shouldReturnNullItems() {
        Order order = Order.builder()
                .id(1L)
                .email("test@test.com")
                .orderStatus(OrderStatus.PENDING)
                .shippingAddressId(OrderAddress.builder().id(1L).build())
                .billingAddressId(OrderAddress.builder().id(2L).build())
                .build();

        OrderVm result = OrderVm.fromModel(order, null);

        assertNotNull(result);
        assertNull(result.orderItemVms());
    }

    @Test
    void fromModel_whenOrderItemsIsEmpty_shouldReturnEmptySet() {
        Order order = Order.builder()
                .id(1L)
                .email("test@test.com")
                .orderStatus(OrderStatus.PENDING)
                .shippingAddressId(OrderAddress.builder().id(1L).build())
                .billingAddressId(OrderAddress.builder().id(2L).build())
                .build();

        OrderVm result = OrderVm.fromModel(order, new HashSet<>());

        assertNotNull(result);
        assertThat(result.orderItemVms()).isEmpty();
    }
}
