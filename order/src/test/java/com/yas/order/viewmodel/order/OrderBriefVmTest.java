package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderBriefVmTest {

    @Test
    void builderShouldCreateOrderBriefVm() {
        OrderAddressVm billingAddressVm = OrderAddressVm.builder()
                .id(11L)
                .contactName("Billing Contact")
                .phone("0123456789")
                .addressLine1("123 Billing Street")
                .addressLine2("Apt 456")
                .city("Ho Chi Minh City")
                .zipCode("700000")
                .districtId(21L)
                .districtName("District 1")
                .stateOrProvinceId(31L)
                .stateOrProvinceName("HCMC")
                .countryId(41L)
                .countryName("Vietnam")
                .build();

        OrderBriefVm orderBriefVm = OrderBriefVm.builder()
                .id(1L)
                .email("customer@example.com")
                .billingAddressVm(billingAddressVm)
                .totalPrice(BigDecimal.valueOf(123.45))
                .orderStatus(OrderStatus.values()[0])
                .deliveryMethod(DeliveryMethod.values()[0])
                .deliveryStatus(DeliveryStatus.values()[0])
                .paymentStatus(PaymentStatus.values()[0])
                .createdOn(ZonedDateTime.parse("2026-04-28T10:15:30Z"))
                .build();

        assertEquals(1L, orderBriefVm.id());
        assertEquals("customer@example.com", orderBriefVm.email());
        assertEquals(billingAddressVm, orderBriefVm.billingAddressVm());
        assertEquals(BigDecimal.valueOf(123.45), orderBriefVm.totalPrice());
        assertEquals(OrderStatus.values()[0], orderBriefVm.orderStatus());
        assertEquals(DeliveryMethod.values()[0], orderBriefVm.deliveryMethod());
        assertEquals(DeliveryStatus.values()[0], orderBriefVm.deliveryStatus());
        assertEquals(PaymentStatus.values()[0], orderBriefVm.paymentStatus());
        assertEquals(ZonedDateTime.parse("2026-04-28T10:15:30Z"), orderBriefVm.createdOn());
    }

    @Test
    void fromModelShouldMapOrderToOrderBriefVm() {
        OrderAddress billingAddress = OrderAddress.builder()
                .id(11L)
                .contactName("Billing Contact")
                .phone("0123456789")
                .addressLine1("123 Billing Street")
                .addressLine2("Apt 456")
                .city("Ho Chi Minh City")
                .zipCode("700000")
                .districtId(21L)
                .districtName("District 1")
                .stateOrProvinceId(31L)
                .stateOrProvinceName("HCMC")
                .countryId(41L)
                .countryName("Vietnam")
                .build();

        ZonedDateTime createdOn = ZonedDateTime.parse("2026-04-28T10:15:30Z");

        Order order = Order.builder()
                .id(1L)
                .email("customer@example.com")
                .billingAddressId(billingAddress)
                .totalPrice(BigDecimal.valueOf(123.45))
                .orderStatus(OrderStatus.values()[0])
                .deliveryMethod(DeliveryMethod.values()[0])
                .deliveryStatus(DeliveryStatus.values()[0])
                .paymentStatus(PaymentStatus.values()[0])
                .build();
        order.setCreatedOn(createdOn);

        OrderBriefVm orderBriefVm = OrderBriefVm.fromModel(order);

        assertEquals(1L, orderBriefVm.id());
        assertEquals("customer@example.com", orderBriefVm.email());
        assertEquals(OrderAddressVm.fromModel(billingAddress), orderBriefVm.billingAddressVm());
        assertEquals(BigDecimal.valueOf(123.45), orderBriefVm.totalPrice());
        assertEquals(OrderStatus.values()[0], orderBriefVm.orderStatus());
        assertEquals(DeliveryMethod.values()[0], orderBriefVm.deliveryMethod());
        assertEquals(DeliveryStatus.values()[0], orderBriefVm.deliveryStatus());
        assertEquals(PaymentStatus.values()[0], orderBriefVm.paymentStatus());
        assertEquals(createdOn, orderBriefVm.createdOn());
    }
}