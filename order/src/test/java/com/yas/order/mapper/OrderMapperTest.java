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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {OrderMapperImpl.class})
class OrderMapperTest {

    @Autowired
    OrderMapper orderMapper;

    @Test
    void toCsv_shouldMapAllFieldsCorrectly() {
        OrderAddressVm billingAddress = OrderAddressVm.builder()
                .id(1L)
                .contactName("Jane Smith")
                .phone("+1234567890")
                .addressLine1("123 Main St")
                .city("Springfield")
                .zipCode("62701")
                .countryName("USA")
                .build();

        OrderBriefVm orderBriefVm = OrderBriefVm.builder()
                .id(100L)
                .email("test@example.com")
                .billingAddressVm(billingAddress)
                .totalPrice(new BigDecimal("199.99"))
                .orderStatus(OrderStatus.COMPLETED)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .paymentStatus(PaymentStatus.COMPLETED)
                .createdOn(ZonedDateTime.now())
                .build();

        var result = orderMapper.toCsv(orderBriefVm);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPhone()).isEqualTo("+1234567890");
        assertThat(result.getTotalPrice()).isEqualTo(new BigDecimal("199.99"));
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void toCsv_whenBillingAddressIsNull_shouldHandleGracefully() {
        OrderBriefVm orderBriefVm = OrderBriefVm.builder()
                .id(200L)
                .email("test@example.com")
                .billingAddressVm(null)
                .totalPrice(new BigDecimal("50.00"))
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        var result = orderMapper.toCsv(orderBriefVm);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(200L);
        assertThat(result.getPhone()).isNull();
    }

    @Test
    void toCsv_shouldMapCreatedOnField() {
        ZonedDateTime now = ZonedDateTime.now();
        OrderBriefVm orderBriefVm = OrderBriefVm.builder()
                .id(300L)
                .email("date@test.com")
                .orderStatus(OrderStatus.PAID)
                .paymentStatus(PaymentStatus.COMPLETED)
                .deliveryStatus(DeliveryStatus.DELIVERING)
                .deliveryMethod(DeliveryMethod.SHOPEE_EXPRESS)
                .totalPrice(new BigDecimal("75.00"))
                .createdOn(now)
                .build();

        var result = orderMapper.toCsv(orderBriefVm);

        assertThat(result.getCreatedOn()).isEqualTo(now);
    }
}
