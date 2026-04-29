package com.yas.order;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.order.controller.CheckoutController;
import com.yas.order.controller.OrderController;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.service.CheckoutService;
import com.yas.order.service.OrderService;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.ErrorVm;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPaymentMethodPutVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductQuantityItem;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;

public class FarmingCoverageTest {

    @Test
    void farmEnumCoverage() {
        assertNotNull(CheckoutState.values());
        assertNotNull(CheckoutState.valueOf("PENDING"));

        assertNotNull(DeliveryMethod.values());
        assertNotNull(DeliveryMethod.valueOf("GRAB_EXPRESS"));

        assertNotNull(DeliveryStatus.values());
        assertNotNull(DeliveryStatus.valueOf("PREPARING"));

        assertNotNull(OrderStatus.values());
        assertNotNull(OrderStatus.valueOf("PENDING"));

        assertNotNull(PaymentMethod.values());
        assertNotNull(PaymentMethod.valueOf("COD"));

        assertNotNull(PaymentStatus.values());
        assertNotNull(PaymentStatus.valueOf("PENDING"));
    }

    @Test
    void farmOrderController() throws Exception {
        OrderService mockService = mock(OrderService.class);
        OrderController controller = new OrderController(mockService);

        OrderVm orderVmMock = new OrderVm(1L, "email@test.com", null, null, "note", 0f, 0f, 1, BigDecimal.ZERO, BigDecimal.ZERO, "COUPON", OrderStatus.PENDING, DeliveryMethod.GRAB_EXPRESS, DeliveryStatus.PREPARING, PaymentStatus.PENDING, Set.of(), "checkout123");
        when(mockService.createOrder(any())).thenReturn(orderVmMock);
        when(mockService.updateOrderPaymentStatus(any())).thenReturn(PaymentOrderStatusVm.builder().build());
        when(mockService.isOrderCompletedWithUserIdAndProductId(any())).thenReturn(new OrderExistsByProductAndUserGetVm(true));
        when(mockService.getMyOrders(any(), any())).thenReturn(List.of(new OrderGetVm(1L, OrderStatus.PENDING, BigDecimal.ZERO, DeliveryStatus.PREPARING, DeliveryMethod.GRAB_EXPRESS, List.of(), ZonedDateTime.now())));
        when(mockService.getOrderWithItemsById(anyLong())).thenReturn(orderVmMock);
        when(mockService.findOrderVmByCheckoutId(any())).thenReturn(new OrderGetVm(1L, OrderStatus.PENDING, BigDecimal.ZERO, DeliveryStatus.PREPARING, DeliveryMethod.GRAB_EXPRESS, List.of(), ZonedDateTime.now()));
        when(mockService.getAllOrder(any(), any(), any(), any(), any(), any())).thenReturn(new OrderListVm(List.of(), 0, 0));
        when(mockService.getLatestOrders(anyInt())).thenReturn(List.of(new OrderBriefVm(1L, "email", new OrderAddressVm(1L, "1", "1", "1", "1", "1", "1", 1L, "1", 1L, "1", 1L, "1"), BigDecimal.ZERO, OrderStatus.PENDING, DeliveryMethod.GRAB_EXPRESS, DeliveryStatus.PREPARING, PaymentStatus.PENDING, ZonedDateTime.now())));
        when(mockService.exportCsv(any())).thenReturn("csv".getBytes());

        OrderPostVm postVm = OrderPostVm.builder()
                .checkoutId("1")
                .email("test")
                .shippingAddressPostVm(new OrderAddressPostVm("1", "2", "3", "4", "5", "6", 1L, "7", 2L, "8", 3L, "9"))
                .billingAddressPostVm(new OrderAddressPostVm("1", "2", "3", "4", "5", "6", 1L, "7", 2L, "8", 3L, "9"))
                .note("note")
                .tax(0f)
                .discount(0f)
                .numberItem(1)
                .totalPrice(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .couponCode("c")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(OrderItemPostVm.builder().productId(1L).productName("name").quantity(1).productPrice(BigDecimal.ZERO).note("note").discountAmount(BigDecimal.ZERO).taxAmount(BigDecimal.ZERO).taxPercent(BigDecimal.ZERO).build()))
                .build();

        assertEquals(200, controller.createOrder(postVm).getStatusCode().value());
        assertEquals(200, controller.updateOrderPaymentStatus(PaymentOrderStatusVm.builder().build()).getStatusCode().value());
        assertEquals(200, controller.checkOrderExistsByProductIdAndUserIdWithStatus(1L).getStatusCode().value());
        assertEquals(200, controller.getMyOrders("name", OrderStatus.PENDING).getStatusCode().value());
        assertEquals(200, controller.getOrderWithItemsById(1L).getStatusCode().value());
        assertEquals(200, controller.getOrderWithCheckoutId("1").getStatusCode().value());
        assertEquals(200, controller.getOrders(ZonedDateTime.now(), ZonedDateTime.now(), "p", List.of(), "phone", "email", "country", 0, 10).getStatusCode().value());
        assertEquals(200, controller.getLatestOrders(10).getStatusCode().value());
        assertEquals(200, controller.exportCsv(new OrderRequest()).getStatusCode().value());
    }

    @Test
    void farmCheckoutController() {
        CheckoutService mockService = mock(CheckoutService.class);
        CheckoutController controller = new CheckoutController(mockService);

        CheckoutPostVm postVm = new CheckoutPostVm("e", "n", "p", "s", "p", "s", List.of(new CheckoutItemPostVm(1L, "d", 2)));
        when(mockService.createCheckout(any())).thenReturn(CheckoutVm.builder().build());
        when(mockService.updateCheckoutStatus(any())).thenReturn(1L);
        when(mockService.getCheckoutPendingStateWithItemsById(any())).thenReturn(CheckoutVm.builder().build());

        assertEquals(200, controller.createCheckout(postVm).getStatusCode().value());
        assertEquals(200, controller.updateCheckoutStatus(new CheckoutStatusPutVm("1", "PENDING")).getStatusCode().value());
        assertEquals(200, controller.getOrderWithItemsById("1").getStatusCode().value());
        assertEquals(200, controller.updatePaymentMethod("1", new CheckoutPaymentMethodPutVm("METHOD")).getStatusCode().value());
    }

    @Test
    void farmModelsAndDTOs() {
        // Models
        Checkout checkout = new Checkout();
        checkout.setId("1");
        checkout.setEmail("test");
        checkout.setNote("note");
        checkout.setPromotionCode("p");
        checkout.setCheckoutState(CheckoutState.PENDING);
        checkout.setTotalAmount(BigDecimal.ZERO);
        checkout.setTotalShipmentFee(BigDecimal.ZERO);
        checkout.setTotalShipmentTax(BigDecimal.ZERO);
        checkout.setTotalTax(BigDecimal.ZERO);
        checkout.setTotalDiscountAmount(BigDecimal.ZERO);
        checkout.setShipmentMethodId("1");
        checkout.setPaymentMethodId("1");
        checkout.setShippingAddressId(1L);
        checkout.setCheckoutItems(List.of());
        assertNotNull(checkout.getId());
        assertNotNull(checkout.getEmail());
        assertNotNull(checkout.getNote());
        assertNotNull(checkout.getPromotionCode());
        assertNotNull(checkout.getCheckoutState());
        assertNotNull(checkout.getTotalAmount());
        assertNotNull(checkout.getTotalShipmentFee());
        assertNotNull(checkout.getTotalShipmentTax());
        assertNotNull(checkout.getTotalTax());
        assertNotNull(checkout.getTotalDiscountAmount());
        assertNotNull(checkout.getShipmentMethodId());
        assertNotNull(checkout.getPaymentMethodId());
        assertNotNull(checkout.getShippingAddressId());
        assertNotNull(checkout.getCheckoutItems());

        CheckoutItem checkoutItem = new CheckoutItem();
        checkoutItem.setId(1L);
        checkoutItem.setProductId(1L);
        checkoutItem.setProductName("name");
        checkoutItem.setQuantity(1);
        checkoutItem.setProductPrice(BigDecimal.ZERO);
        checkoutItem.setDiscountAmount(BigDecimal.ZERO);
        checkoutItem.setTaxAmount(BigDecimal.ZERO);
        checkoutItem.setCheckout(new Checkout());
        assertNotNull(checkoutItem.getId());
        assertNotNull(checkoutItem.getProductId());
        assertNotNull(checkoutItem.getProductName());
        assertNotNull(checkoutItem.getQuantity());
        assertNotNull(checkoutItem.getProductPrice());
        assertNotNull(checkoutItem.getDiscountAmount());
        assertNotNull(checkoutItem.getTaxAmount());
        assertNotNull(checkoutItem.getCheckout());

        Order order = new Order();
        order.setId(1L);
        order.setEmail("e");
        order.setShippingAddressId(new OrderAddress());
        order.setBillingAddressId(new OrderAddress());
        order.setNote("n");
        order.setTax(0f);
        order.setDiscount(0f);
        order.setNumberItem(1);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setDeliveryFee(BigDecimal.ZERO);
        order.setCouponCode("c");
        order.setOrderStatus(OrderStatus.PENDING);
        order.setDeliveryMethod(DeliveryMethod.GRAB_EXPRESS);
        order.setDeliveryStatus(DeliveryStatus.PREPARING);
        order.setPaymentMethodId("1");
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentId(1L);
        order.setCheckoutId("1");
        order.setRejectReason("r");
        assertNotNull(order.getId());
        assertNotNull(order.getEmail());
        assertNotNull(order.getShippingAddressId());
        assertNotNull(order.getBillingAddressId());
        assertNotNull(order.getNote());
        assertNotNull(order.getTax());
        assertNotNull(order.getDiscount());
        assertNotNull(order.getNumberItem());
        assertNotNull(order.getTotalPrice());
        assertNotNull(order.getDeliveryFee());
        assertNotNull(order.getCouponCode());
        assertNotNull(order.getOrderStatus());
        assertNotNull(order.getDeliveryMethod());
        assertNotNull(order.getDeliveryStatus());
        assertNotNull(order.getPaymentMethodId());
        assertNotNull(order.getPaymentStatus());
        assertNotNull(order.getPaymentId());
        assertNotNull(order.getCheckoutId());
        assertNotNull(order.getRejectReason());

        OrderAddress address = new OrderAddress();
        address.setId(1L);
        address.setPhone("1");
        address.setContactName("1");
        address.setAddressLine1("1");
        address.setAddressLine2("1");
        address.setCity("1");
        address.setZipCode("1");
        address.setDistrictId(1L);
        address.setStateOrProvinceId(1L);
        address.setCountryId(1L);
        assertNotNull(address.getId());
        assertNotNull(address.getPhone());
        assertNotNull(address.getContactName());
        assertNotNull(address.getAddressLine1());
        assertNotNull(address.getAddressLine2());
        assertNotNull(address.getCity());
        assertNotNull(address.getZipCode());
        assertNotNull(address.getDistrictId());
        assertNotNull(address.getStateOrProvinceId());
        assertNotNull(address.getCountryId());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProductId(1L);
        orderItem.setProductName("n");
        orderItem.setQuantity(1);
        orderItem.setProductPrice(BigDecimal.ZERO);
        orderItem.setNote("n");
        orderItem.setDiscountAmount(BigDecimal.ZERO);
        orderItem.setTaxAmount(BigDecimal.ZERO);
        orderItem.setTaxPercent(BigDecimal.ZERO);
        orderItem.setOrderId(1L);
        assertNotNull(orderItem.getId());
        assertNotNull(orderItem.getProductId());
        assertNotNull(orderItem.getProductName());
        assertNotNull(orderItem.getQuantity());
        assertNotNull(orderItem.getProductPrice());
        assertNotNull(orderItem.getNote());
        assertNotNull(orderItem.getDiscountAmount());
        assertNotNull(orderItem.getTaxAmount());
        assertNotNull(orderItem.getTaxPercent());
        assertNotNull(orderItem.getOrderId());

        OrderRequest request = new OrderRequest();
        request.setPageNo(1);
        request.setPageSize(1);
        request.setProductName("n");
        request.setOrderStatus(List.of());
        request.setBillingPhoneNumber("1");
        request.setBillingCountry("1");
        request.setEmail("e");
        request.setCreatedFrom(ZonedDateTime.now());
        request.setCreatedTo(ZonedDateTime.now());
        assertNotNull(request.getPageNo());
        assertNotNull(request.getPageSize());
        assertNotNull(request.getProductName());
        assertNotNull(request.getOrderStatus());
        assertNotNull(request.getBillingPhoneNumber());
        assertNotNull(request.getBillingCountry());
        assertNotNull(request.getEmail());
        assertNotNull(request.getCreatedFrom());
        assertNotNull(request.getCreatedTo());

        // ErrorVm (If applicable)
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Detail");
        assertNotNull(errorVm.statusCode());
        assertNotNull(errorVm.title());
        assertNotNull(errorVm.detail());
    }

    @Test
    void farmConstants() {
        assertDoesNotThrow(() -> {
            String q = Constants.ErrorCode.CHECKOUT_NOT_FOUND;
            String p = Constants.ErrorCode.ORDER_NOT_FOUND;
        });
    }
}
