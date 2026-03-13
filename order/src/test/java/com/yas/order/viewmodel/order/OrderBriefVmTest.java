package com.yas.order.viewmodel.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class OrderBriefVmTest {

    @Test
    void fromModel_shouldMapAllFieldsCorrectly() {
        OrderAddress billingAddress = OrderAddress.builder()
                .id(1L).contactName("Jane").phone("123").addressLine1("Addr")
                .city("City").zipCode("12345").districtId(1L).districtName("D")
                .stateOrProvinceId(1L).stateOrProvinceName("S").countryId(1L).countryName("C")
                .build();

        Order order = Order.builder()
                .id(1L)
                .email("test@test.com")
                .billingAddressId(billingAddress)
                .totalPrice(new BigDecimal("200.00"))
                .orderStatus(OrderStatus.COMPLETED)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
        order.setCreatedOn(ZonedDateTime.now());

        OrderBriefVm result = OrderBriefVm.fromModel(order);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("test@test.com", result.email());
        assertNotNull(result.billingAddressVm());
        assertEquals(new BigDecimal("200.00"), result.totalPrice());
        assertEquals(OrderStatus.COMPLETED, result.orderStatus());
        assertEquals(DeliveryMethod.GRAB_EXPRESS, result.deliveryMethod());
        assertEquals(DeliveryStatus.DELIVERED, result.deliveryStatus());
        assertEquals(PaymentStatus.COMPLETED, result.paymentStatus());
        assertNotNull(result.createdOn());
    }

    @Test
    void builder_shouldCreateValidVm() {
        OrderBriefVm vm = OrderBriefVm.builder()
                .id(2L)
                .email("builder@test.com")
                .totalPrice(new BigDecimal("50.00"))
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        assertNotNull(vm);
        assertEquals(2L, vm.id());
        assertEquals("builder@test.com", vm.email());
    }
}
