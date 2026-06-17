package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderVmTest {

    @Test
    void builderShouldCreateOrderVm() {
        OrderAddressVm shippingAddressVm = OrderAddressVm.builder()
                .id(21L)
                .contactName("Shipping Contact")
                .phone("0987654321")
                .addressLine1("456 Shipping Street")
                .addressLine2("Floor 7")
                .city("Da Nang")
                .zipCode("550000")
                .districtId(22L)
                .districtName("Hai Chau")
                .stateOrProvinceId(32L)
                .stateOrProvinceName("Da Nang")
                .countryId(42L)
                .countryName("Vietnam")
                .build();
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
        OrderItemVm orderItemVm = OrderItemVm.builder()
                .id(1L)
                .productId(100L)
                .productName("Test Product")
                .quantity(2)
                .productPrice(BigDecimal.valueOf(10.50))
                .note("Test note")
                .discountAmount(BigDecimal.valueOf(1.50))
                .taxAmount(BigDecimal.valueOf(0.75))
                .taxPercent(BigDecimal.valueOf(0.10))
                .orderId(1L)
                .build();

        OrderVm orderVm = OrderVm.builder()
                .id(1L)
                .email("customer@example.com")
                .shippingAddressVm(shippingAddressVm)
                .billingAddressVm(billingAddressVm)
                .note("Leave at door")
                .tax(1.25f)
                .discount(0.50f)
                .numberItem(1)
                .totalPrice(BigDecimal.valueOf(123.45))
                .deliveryFee(BigDecimal.valueOf(15.00))
                .couponCode("WELCOME10")
                .orderStatus(OrderStatus.values()[0])
                .deliveryMethod(DeliveryMethod.values()[0])
                .deliveryStatus(DeliveryStatus.values()[0])
                .paymentStatus(PaymentStatus.values()[0])
                .orderItemVms(Set.of(orderItemVm))
                .checkoutId("checkout-1")
                .build();

        assertEquals(1L, orderVm.id());
        assertEquals("customer@example.com", orderVm.email());
        assertEquals(shippingAddressVm, orderVm.shippingAddressVm());
        assertEquals(billingAddressVm, orderVm.billingAddressVm());
        assertEquals("Leave at door", orderVm.note());
        assertEquals(1.25f, orderVm.tax());
        assertEquals(0.50f, orderVm.discount());
        assertEquals(1, orderVm.numberItem());
        assertEquals(BigDecimal.valueOf(123.45), orderVm.totalPrice());
        assertEquals(BigDecimal.valueOf(15.00), orderVm.deliveryFee());
        assertEquals("WELCOME10", orderVm.couponCode());
        assertEquals(OrderStatus.values()[0], orderVm.orderStatus());
        assertEquals(DeliveryMethod.values()[0], orderVm.deliveryMethod());
        assertEquals(DeliveryStatus.values()[0], orderVm.deliveryStatus());
        assertEquals(PaymentStatus.values()[0], orderVm.paymentStatus());
        assertEquals(Set.of(orderItemVm), orderVm.orderItemVms());
        assertEquals("checkout-1", orderVm.checkoutId());
    }

    @Test
    void fromModelShouldMapOrderAndOrderItemsToOrderVm() {
        OrderAddress shippingAddress = OrderAddress.builder()
                .id(21L)
                .contactName("Shipping Contact")
                .phone("0987654321")
                .addressLine1("456 Shipping Street")
                .addressLine2("Floor 7")
                .city("Da Nang")
                .zipCode("550000")
                .districtId(22L)
                .districtName("Hai Chau")
                .stateOrProvinceId(32L)
                .stateOrProvinceName("Da Nang")
                .countryId(42L)
                .countryName("Vietnam")
                .build();
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
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Test Product")
                .quantity(2)
                .productPrice(BigDecimal.valueOf(10.50))
                .note("Test note")
                .discountAmount(BigDecimal.valueOf(1.50))
                .taxAmount(BigDecimal.valueOf(0.75))
                .taxPercent(BigDecimal.valueOf(0.10))
                .orderId(1L)
                .build();

        Order order = Order.builder()
                .id(1L)
                .email("customer@example.com")
                .shippingAddressId(shippingAddress)
                .billingAddressId(billingAddress)
                .note("Leave at door")
                .tax(1.25f)
                .discount(0.50f)
                .numberItem(1)
                .totalPrice(BigDecimal.valueOf(123.45))
                .deliveryFee(BigDecimal.valueOf(15.00))
                .couponCode("WELCOME10")
                .orderStatus(OrderStatus.values()[0])
                .deliveryMethod(DeliveryMethod.values()[0])
                .deliveryStatus(DeliveryStatus.values()[0])
                .paymentStatus(PaymentStatus.values()[0])
                .checkoutId("checkout-1")
                .build();

        OrderVm orderVm = OrderVm.fromModel(order, Set.of(orderItem));

        assertEquals(1L, orderVm.id());
        assertEquals("customer@example.com", orderVm.email());
        assertEquals(OrderAddressVm.fromModel(shippingAddress), orderVm.shippingAddressVm());
        assertEquals(OrderAddressVm.fromModel(billingAddress), orderVm.billingAddressVm());
        assertEquals("Leave at door", orderVm.note());
        assertEquals(1.25f, orderVm.tax());
        assertEquals(0.50f, orderVm.discount());
        assertEquals(1, orderVm.numberItem());
        assertEquals(BigDecimal.valueOf(123.45), orderVm.totalPrice());
        assertEquals(BigDecimal.valueOf(15.00), orderVm.deliveryFee());
        assertEquals("WELCOME10", orderVm.couponCode());
        assertEquals(OrderStatus.values()[0], orderVm.orderStatus());
        assertEquals(DeliveryMethod.values()[0], orderVm.deliveryMethod());
        assertEquals(DeliveryStatus.values()[0], orderVm.deliveryStatus());
        assertEquals(PaymentStatus.values()[0], orderVm.paymentStatus());
        assertEquals(Set.of(OrderItemVm.fromModel(orderItem)), orderVm.orderItemVms());
        assertEquals("checkout-1", orderVm.checkoutId());
    }
}