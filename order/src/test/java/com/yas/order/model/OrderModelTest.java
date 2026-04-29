package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderModelTest {

    @Test
    void testCheckoutBuilder() {
        Checkout checkout = Checkout.builder()
                .id("checkout-1")
                .email("test@example.com")
                .note("note")
                .promotionCode("PROMO10")
                .checkoutState(CheckoutState.PENDING)
                .progress("progress")
                .customerId("cust-1")
                .shipmentMethodId("ship-1")
                .paymentMethodId("pay-1")
                .shippingAddressId(1L)
                .lastError("{}")
                .attributes("{}")
                .totalAmount(BigDecimal.TEN)
                .totalShipmentFee(BigDecimal.ONE)
                .totalShipmentTax(BigDecimal.ZERO)
                .totalTax(BigDecimal.valueOf(2))
                .totalDiscountAmount(BigDecimal.valueOf(3))
                .checkoutItems(new ArrayList<>())
                .build();

        assertEquals("checkout-1", checkout.getId());
        assertEquals("test@example.com", checkout.getEmail());
        assertEquals("note", checkout.getNote());
        assertEquals("PROMO10", checkout.getPromotionCode());
        assertEquals(CheckoutState.PENDING, checkout.getCheckoutState());
        assertEquals("progress", checkout.getProgress());
        assertEquals("cust-1", checkout.getCustomerId());
        assertEquals("ship-1", checkout.getShipmentMethodId());
        assertEquals("pay-1", checkout.getPaymentMethodId());
        assertEquals(1L, checkout.getShippingAddressId());
        assertEquals("{}", checkout.getLastError());
        assertEquals("{}", checkout.getAttributes());
        assertEquals(BigDecimal.TEN, checkout.getTotalAmount());
        assertEquals(BigDecimal.ONE, checkout.getTotalShipmentFee());
        assertEquals(BigDecimal.ZERO, checkout.getTotalShipmentTax());
        assertEquals(BigDecimal.valueOf(2), checkout.getTotalTax());
        assertEquals(BigDecimal.valueOf(3), checkout.getTotalDiscountAmount());
        assertNotNull(checkout.getCheckoutItems());
    }

    @Test
    void testCheckoutSetters() {
        Checkout checkout = new Checkout();
        checkout.setId("id1");
        checkout.setEmail("email@test.com");
        checkout.setNote("my note");
        checkout.setPromotionCode("CODE");
        checkout.setCheckoutState(CheckoutState.COMPLETED);
        checkout.setPaymentMethodId("pm-1");
        checkout.setTotalAmount(BigDecimal.valueOf(100));
        checkout.setCheckoutItems(List.of());

        assertEquals("id1", checkout.getId());
        assertEquals("email@test.com", checkout.getEmail());
        assertEquals("my note", checkout.getNote());
        assertEquals("CODE", checkout.getPromotionCode());
        assertEquals(CheckoutState.COMPLETED, checkout.getCheckoutState());
        assertEquals("pm-1", checkout.getPaymentMethodId());
        assertEquals(BigDecimal.valueOf(100), checkout.getTotalAmount());
        assertNotNull(checkout.getCheckoutItems());
    }

    @Test
    void testCheckoutDefaultValues() {
        Checkout checkout = Checkout.builder().build();
        assertEquals(BigDecimal.ZERO, checkout.getTotalAmount());
        assertEquals(BigDecimal.ZERO, checkout.getTotalShipmentFee());
        assertEquals(BigDecimal.ZERO, checkout.getTotalShipmentTax());
        assertEquals(BigDecimal.ZERO, checkout.getTotalDiscountAmount());
        assertNotNull(checkout.getCheckoutItems());
    }

    @Test
    void testOrderBuilder() {
        OrderAddress shippingAddr = OrderAddress.builder().id(1L).build();
        OrderAddress billingAddr = OrderAddress.builder().id(2L).build();

        Order order = Order.builder()
                .id(1L)
                .email("order@test.com")
                .shippingAddressId(shippingAddr)
                .billingAddressId(billingAddr)
                .note("order note")
                .tax(1.5f)
                .discount(2.0f)
                .numberItem(3)
                .couponCode("COUPON")
                .totalPrice(BigDecimal.valueOf(100))
                .deliveryFee(BigDecimal.valueOf(5))
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentId(10L)
                .checkoutId("checkout-1")
                .rejectReason("none")
                .paymentMethodId("pm-1")
                .progress("in-progress")
                .customerId("cust-1")
                .lastError("{}")
                .attributes("{}")
                .totalShipmentTax(BigDecimal.ONE)
                .build();

        assertEquals(1L, order.getId());
        assertEquals("order@test.com", order.getEmail());
        assertNotNull(order.getShippingAddressId());
        assertNotNull(order.getBillingAddressId());
        assertEquals("order note", order.getNote());
        assertEquals(1.5f, order.getTax());
        assertEquals(2.0f, order.getDiscount());
        assertEquals(3, order.getNumberItem());
        assertEquals("COUPON", order.getCouponCode());
        assertEquals(BigDecimal.valueOf(100), order.getTotalPrice());
        assertEquals(BigDecimal.valueOf(5), order.getDeliveryFee());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(DeliveryMethod.GRAB_EXPRESS, order.getDeliveryMethod());
        assertEquals(DeliveryStatus.PREPARING, order.getDeliveryStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        assertEquals(10L, order.getPaymentId());
        assertEquals("checkout-1", order.getCheckoutId());
        assertEquals("none", order.getRejectReason());
        assertEquals("pm-1", order.getPaymentMethodId());
        assertEquals("in-progress", order.getProgress());
        assertEquals("cust-1", order.getCustomerId());
        assertEquals("{}", order.getLastError());
        assertEquals("{}", order.getAttributes());
        assertEquals(BigDecimal.ONE, order.getTotalShipmentTax());
    }

    @Test
    void testOrderSetters() {
        Order order = new Order();
        order.setId(5L);
        order.setEmail("email@test.com");
        order.setOrderStatus(OrderStatus.PAID);
        order.setPaymentId(99L);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setRejectReason("bad item");
        order.setCheckoutId("chk-99");

        assertEquals(5L, order.getId());
        assertEquals("email@test.com", order.getEmail());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(99L, order.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, order.getPaymentStatus());
        assertEquals("bad item", order.getRejectReason());
        assertEquals("chk-99", order.getCheckoutId());
    }

    @Test
    void testOrderAddressBuilder() {
        OrderAddress addr = OrderAddress.builder()
                .id(1L)
                .contactName("John Doe")
                .phone("0123456789")
                .addressLine1("123 Main St")
                .addressLine2("Apt 4")
                .city("Hanoi")
                .zipCode("10000")
                .districtId(10L)
                .districtName("Ba Dinh")
                .stateOrProvinceId(20L)
                .stateOrProvinceName("Hanoi")
                .countryId(30L)
                .countryName("Vietnam")
                .build();

        assertEquals(1L, addr.getId());
        assertEquals("John Doe", addr.getContactName());
        assertEquals("0123456789", addr.getPhone());
        assertEquals("123 Main St", addr.getAddressLine1());
        assertEquals("Apt 4", addr.getAddressLine2());
        assertEquals("Hanoi", addr.getCity());
        assertEquals("10000", addr.getZipCode());
        assertEquals(10L, addr.getDistrictId());
        assertEquals("Ba Dinh", addr.getDistrictName());
        assertEquals(20L, addr.getStateOrProvinceId());
        assertEquals("Hanoi", addr.getStateOrProvinceName());
        assertEquals(30L, addr.getCountryId());
        assertEquals("Vietnam", addr.getCountryName());
    }

    @Test
    void testOrderAddressSetters() {
        OrderAddress addr = new OrderAddress();
        addr.setId(2L);
        addr.setContactName("Jane");
        addr.setPhone("999");
        addr.setCity("HCMC");

        assertEquals(2L, addr.getId());
        assertEquals("Jane", addr.getContactName());
        assertEquals("999", addr.getPhone());
        assertEquals("HCMC", addr.getCity());
    }

    @Test
    void testOrderItemBuilder() {
        OrderItem item = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .orderId(10L)
                .productName("Product X")
                .quantity(3)
                .productPrice(BigDecimal.valueOf(25.0))
                .note("item note")
                .discountAmount(BigDecimal.ONE)
                .taxAmount(BigDecimal.valueOf(2))
                .taxPercent(BigDecimal.valueOf(0.1))
                .shipmentFee(BigDecimal.valueOf(3))
                .status("ACTIVE")
                .shipmentTax(BigDecimal.valueOf(0.5))
                .processingState("{}")
                .build();

        assertEquals(1L, item.getId());
        assertEquals(100L, item.getProductId());
        assertEquals(10L, item.getOrderId());
        assertEquals("Product X", item.getProductName());
        assertEquals(3, item.getQuantity());
        assertEquals(BigDecimal.valueOf(25.0), item.getProductPrice());
        assertEquals("item note", item.getNote());
        assertEquals(BigDecimal.ONE, item.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(2), item.getTaxAmount());
        assertEquals(BigDecimal.valueOf(0.1), item.getTaxPercent());
        assertEquals(BigDecimal.valueOf(3), item.getShipmentFee());
        assertEquals("ACTIVE", item.getStatus());
        assertEquals(BigDecimal.valueOf(0.5), item.getShipmentTax());
        assertEquals("{}", item.getProcessingState());
        assertNull(item.getOrder());
    }

    @Test
    void testOrderItemSetters() {
        OrderItem item = new OrderItem();
        item.setId(5L);
        item.setProductId(50L);
        item.setOrderId(20L);
        item.setProductName("Updated Product");
        item.setQuantity(10);
        item.setProductPrice(BigDecimal.valueOf(99));

        assertEquals(5L, item.getId());
        assertEquals(50L, item.getProductId());
        assertEquals(20L, item.getOrderId());
        assertEquals("Updated Product", item.getProductName());
        assertEquals(10, item.getQuantity());
        assertEquals(BigDecimal.valueOf(99), item.getProductPrice());
    }

    @Test
    void testOrderAllArgsConstructor() {
        OrderAddress addr = OrderAddress.builder().id(1L).build();
        Order order = new Order(1L, "e@e.com", addr, addr, "n", 0.1f, 0.2f, 1,
                "C", BigDecimal.TEN, BigDecimal.ONE, OrderStatus.PENDING,
                DeliveryMethod.GRAB_EXPRESS, DeliveryStatus.PREPARING,
                PaymentStatus.PENDING, 1L, "chk", "reason", "pm", "prog",
                "cust", "{}", "{}", BigDecimal.ZERO);
        assertNotNull(order);
        assertEquals(1L, order.getId());
    }
}
