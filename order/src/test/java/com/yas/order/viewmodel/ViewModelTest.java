package com.yas.order.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.cart.CartItemDeleteVm;
import com.yas.order.viewmodel.checkout.*;
import com.yas.order.viewmodel.customer.CustomerVm;
import com.yas.order.viewmodel.order.*;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import com.yas.order.viewmodel.product.*;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void testErrorVmWithFieldErrors() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "detail", List.of("field1"));
        assertEquals("400", vm.statusCode());
        assertEquals("Bad Request", vm.title());
        assertEquals("detail", vm.detail());
        assertEquals(1, vm.fieldErrors().size());
    }

    @Test
    void testErrorVmWithoutFieldErrors() {
        ErrorVm vm = new ErrorVm("500", "Error", "detail");
        assertNotNull(vm.fieldErrors());
        assertTrue(vm.fieldErrors().isEmpty());
    }

    @Test
    void testResponeStatusVm() {
        ResponeStatusVm vm = new ResponeStatusVm("OK", "Success", "200");
        assertEquals("OK", vm.title());
        assertEquals("Success", vm.message());
        assertEquals("200", vm.statusCode());
    }

    @Test
    void testCartItemDeleteVm() {
        CartItemDeleteVm vm = new CartItemDeleteVm(1L, 5);
        assertEquals(1L, vm.productId());
        assertEquals(5, vm.quantity());
    }

    @Test
    void testCustomerVm() {
        CustomerVm vm = new CustomerVm("user1", "email@test.com", "John", "Doe");
        assertEquals("user1", vm.username());
        assertEquals("email@test.com", vm.email());
        assertEquals("John", vm.firstName());
        assertEquals("Doe", vm.lastName());
    }

    @Test
    void testCheckoutItemPostVm() {
        CheckoutItemPostVm vm = new CheckoutItemPostVm(1L, "desc", 3);
        assertEquals(1L, vm.productId());
        assertEquals("desc", vm.description());
        assertEquals(3, vm.quantity());
    }

    @Test
    void testCheckoutItemVmBuilder() {
        CheckoutItemVm vm = CheckoutItemVm.builder()
                .id(1L).productId(2L).productName("P").description("D")
                .quantity(3).productPrice(BigDecimal.TEN)
                .taxAmount(BigDecimal.ONE).discountAmount(BigDecimal.ZERO)
                .shipmentFee(BigDecimal.valueOf(5)).shipmentTax(BigDecimal.valueOf(0.5))
                .checkoutId("chk-1").build();

        assertEquals(1L, vm.id());
        assertEquals(2L, vm.productId());
        assertEquals("P", vm.productName());
        assertEquals(3, vm.quantity());
        assertEquals("chk-1", vm.checkoutId());
    }

    @Test
    void testCheckoutPaymentMethodPutVm() {
        CheckoutPaymentMethodPutVm vm = new CheckoutPaymentMethodPutVm("pm-1");
        assertEquals("pm-1", vm.paymentMethodId());
    }

    @Test
    void testCheckoutPostVm() {
        CheckoutItemPostVm item = new CheckoutItemPostVm(1L, "desc", 2);
        CheckoutPostVm vm = new CheckoutPostVm("email@t.com", "note", "PROMO",
                "ship-1", "pay-1", "shipping-1", List.of(item));
        assertEquals("email@t.com", vm.email());
        assertEquals("note", vm.note());
        assertEquals("PROMO", vm.promotionCode());
        assertEquals(1, vm.checkoutItemPostVms().size());
    }

    @Test
    void testCheckoutStatusPutVm() {
        CheckoutStatusPutVm vm = new CheckoutStatusPutVm("chk-1", "COMPLETED");
        assertEquals("chk-1", vm.checkoutId());
        assertEquals("COMPLETED", vm.checkoutStatus());
    }

    @Test
    void testCheckoutVmBuilder() {
        CheckoutVm vm = CheckoutVm.builder()
                .id("1").email("e").note("n").promotionCode("P")
                .checkoutItemVms(Set.of()).build();
        assertEquals("1", vm.id());
        assertEquals("e", vm.email());

        CheckoutVm rebuilt = vm.toBuilder().email("new").build();
        assertEquals("new", rebuilt.email());
    }

    @Test
    void testOrderExistsByProductAndUserGetVm() {
        OrderExistsByProductAndUserGetVm vm = new OrderExistsByProductAndUserGetVm(true);
        assertTrue(vm.isPresent());
    }

    @Test
    void testOrderListVm() {
        OrderListVm vm = new OrderListVm(List.of(), 0, 0);
        assertNotNull(vm.orderList());
        assertEquals(0, vm.totalElements());

        OrderListVm rebuilt = vm.toBuilder().totalElements(5).build();
        assertEquals(5, rebuilt.totalElements());
    }

    @Test
    void testProductVariationVm() {
        ProductVariationVm vm = new ProductVariationVm(1L, "name", "sku-1");
        assertEquals(1L, vm.id());
        assertEquals("name", vm.name());
        assertEquals("sku-1", vm.sku());
    }

    @Test
    void testProductGetCheckoutListVm() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                List.of(), 0, 10, 0, 0, true);
        assertEquals(0, vm.pageNo());
        assertEquals(10, vm.pageSize());
        assertTrue(vm.isLast());
    }

    @Test
    void testProductQuantityItem() {
        ProductQuantityItem vm = ProductQuantityItem.builder()
                .productId(1L).quantity(10L).build();
        assertEquals(1L, vm.productId());
        assertEquals(10L, vm.quantity());
    }

    @Test
    void testProductCheckoutListVm() {
        ProductCheckoutListVm vm = ProductCheckoutListVm.builder()
                .id(1L).name("Product").price(9.99).taxClassId(2L).build();
        assertEquals(1L, vm.getId());
        assertEquals("Product", vm.getName());
        assertEquals(9.99, vm.getPrice());
        assertEquals(2L, vm.getTaxClassId());

        ProductCheckoutListVm copy = vm.toBuilder().name("Updated").build();
        assertEquals("Updated", copy.getName());
    }

    @Test
    void testPromotionUsageVm() {
        PromotionUsageVm vm = PromotionUsageVm.builder()
                .promotionCode("CODE").productId(1L).userId("u1").orderId(10L).build();
        assertEquals("CODE", vm.promotionCode());
        assertEquals(1L, vm.productId());
        assertEquals("u1", vm.userId());
        assertEquals(10L, vm.orderId());
    }

    @Test
    void testOrderAddressPostVm() {
        OrderAddressPostVm vm = new OrderAddressPostVm("John", "123", "line1",
                "line2", "city", "zip", 1L, "dist", 2L, "state", 3L, "country");
        assertEquals("John", vm.contactName());
        assertEquals("123", vm.phone());
        assertEquals("line1", vm.addressLine1());
    }

    @Test
    void testOrderAddressVmFromModel() {
        OrderAddress addr = OrderAddress.builder()
                .id(1L).contactName("Name").phone("123")
                .addressLine1("a1").addressLine2("a2").city("city")
                .zipCode("zip").districtId(10L).districtName("dist")
                .stateOrProvinceId(20L).stateOrProvinceName("state")
                .countryId(30L).countryName("country").build();

        OrderAddressVm vm = OrderAddressVm.fromModel(addr);
        assertEquals(1L, vm.id());
        assertEquals("Name", vm.contactName());
        assertEquals("123", vm.phone());
        assertEquals("country", vm.countryName());
    }

    @Test
    void testPaymentOrderStatusVm() {
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(1L).orderStatus("PAID").paymentId(10L)
                .paymentStatus("COMPLETED").build();
        assertEquals(1L, vm.orderId());
        assertEquals("PAID", vm.orderStatus());
    }

    @Test
    void testOrderItemPostVm() {
        OrderItemPostVm vm = OrderItemPostVm.builder()
                .productId(1L).productName("P").quantity(2)
                .productPrice(BigDecimal.TEN).note("n")
                .discountAmount(BigDecimal.ONE).taxAmount(BigDecimal.ZERO)
                .taxPercent(BigDecimal.valueOf(0.1)).build();
        assertEquals(1L, vm.productId());
        assertEquals("P", vm.productName());
        assertEquals(2, vm.quantity());
    }

    @Test
    void testOrderItemVmFromModel() {
        OrderItem item = OrderItem.builder()
                .id(1L).productId(2L).productName("P").quantity(3)
                .productPrice(BigDecimal.TEN).note("n")
                .discountAmount(BigDecimal.ONE)
                .taxAmount(BigDecimal.valueOf(2))
                .taxPercent(BigDecimal.valueOf(0.1))
                .orderId(10L).build();

        OrderItemVm vm = OrderItemVm.fromModel(item);
        assertEquals(1L, vm.id());
        assertEquals(2L, vm.productId());
        assertEquals("P", vm.productName());
        assertEquals(10L, vm.orderId());
    }

    @Test
    void testOrderItemGetVmFromModel() {
        OrderItem item = OrderItem.builder()
                .id(1L).productId(2L).productName("P").quantity(3)
                .productPrice(BigDecimal.TEN)
                .discountAmount(BigDecimal.ONE)
                .taxAmount(BigDecimal.valueOf(2)).build();

        OrderItemGetVm vm = OrderItemGetVm.fromModel(item);
        assertEquals(1L, vm.id());
        assertEquals(2L, vm.productId());
    }

    @Test
    void testOrderItemGetVmFromModelsEmpty() {
        List<OrderItemGetVm> result = OrderItemGetVm.fromModels(null);
        assertTrue(result.isEmpty());

        List<OrderItemGetVm> result2 = OrderItemGetVm.fromModels(Collections.emptySet());
        assertTrue(result2.isEmpty());
    }

    @Test
    void testOrderItemGetVmFromModelsWithItems() {
        OrderItem item = OrderItem.builder().id(1L).productId(2L)
                .productName("P").quantity(1).productPrice(BigDecimal.TEN).build();
        List<OrderItemGetVm> result = OrderItemGetVm.fromModels(Set.of(item));
        assertEquals(1, result.size());
    }

    @Test
    void testOrderVmFromModel() {
        OrderAddress addr = OrderAddress.builder().id(1L).contactName("c")
                .phone("p").addressLine1("a1").city("city").zipCode("z")
                .districtId(1L).stateOrProvinceId(2L).countryId(3L).build();
        Order order = Order.builder().id(1L).email("e").note("n")
                .shippingAddressId(addr).billingAddressId(addr)
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .totalPrice(BigDecimal.TEN).checkoutId("chk").build();

        OrderVm vm = OrderVm.fromModel(order, null);
        assertEquals(1L, vm.id());
        assertEquals("e", vm.email());

        OrderItem item = OrderItem.builder().id(1L).productId(2L)
                .productName("P").quantity(1).productPrice(BigDecimal.TEN)
                .orderId(1L).build();
        OrderVm vm2 = OrderVm.fromModel(order, Set.of(item));
        assertEquals(1, vm2.orderItemVms().size());
    }

    @Test
    void testOrderBriefVmFromModel() {
        OrderAddress addr = OrderAddress.builder().id(1L).contactName("c")
                .phone("p").addressLine1("a1").city("city").zipCode("z")
                .districtId(1L).stateOrProvinceId(2L).countryId(3L).build();
        Order order = Order.builder().id(1L).email("e")
                .billingAddressId(addr)
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .totalPrice(BigDecimal.TEN).build();

        OrderBriefVm vm = OrderBriefVm.fromModel(order);
        assertEquals(1L, vm.id());
        assertEquals("e", vm.email());
    }

    @Test
    void testOrderGetVmFromModel() {
        OrderAddress addr = OrderAddress.builder().id(1L).contactName("c")
                .phone("p").addressLine1("a1").city("city").zipCode("z")
                .districtId(1L).stateOrProvinceId(2L).countryId(3L).build();
        Order order = Order.builder().id(1L).email("e")
                .billingAddressId(addr).shippingAddressId(addr)
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .totalPrice(BigDecimal.TEN).build();

        OrderGetVm vm = OrderGetVm.fromModel(order, null);
        assertEquals(1L, vm.id());
        assertTrue(vm.orderItems().isEmpty());
    }

    @Test
    void testOrderPostVmBuilder() {
        OrderAddressPostVm addr = new OrderAddressPostVm("c", "p", "a1",
                "a2", "city", "zip", 1L, "d", 2L, "s", 3L, "co");
        OrderPostVm vm = OrderPostVm.builder()
                .checkoutId("chk").email("e")
                .shippingAddressPostVm(addr).billingAddressPostVm(addr)
                .note("n").tax(0.1f).discount(0.2f).numberItem(1)
                .totalPrice(BigDecimal.TEN).deliveryFee(BigDecimal.ONE)
                .couponCode("C")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(com.yas.order.model.enumeration.PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of()).build();

        assertEquals("chk", vm.checkoutId());
        assertEquals("e", vm.email());
    }
}
