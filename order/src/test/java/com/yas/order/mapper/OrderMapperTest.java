package com.yas.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapperImpl();

    @Test
    void toCsv_shouldMapExpectedFields() {
        OrderBriefVm vm = OrderBriefVm.builder()
                .id(101L)
                .email("alice@example.com")
                .billingAddressVm(OrderAddressVm.builder().phone("0123456789").build())
                .totalPrice(new BigDecimal("120.50"))
                .orderStatus(OrderStatus.PAID)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentStatus(PaymentStatus.COMPLETED)
                .createdOn(ZonedDateTime.now())
                .build();

        var csv = orderMapper.toCsv(vm);

        assertThat(csv.getId()).isEqualTo(101L);
        assertThat(csv.getEmail()).isEqualTo("alice@example.com");
        assertThat(csv.getPhone()).isEqualTo("0123456789");
    }
}
