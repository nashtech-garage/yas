package com.yas.order.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import com.yas.order.viewmodel.product.ProductQuantityItem;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderViewModelTest {

    @Test
    void mappingMethods_shouldMapModelData() {
        Order order = createOrder();
        OrderItem item = createOrderItem(order.getId());

        OrderAddressVm addressVm = OrderAddressVm.fromModel(order.getBillingAddressId());
        OrderBriefVm briefVm = OrderBriefVm.fromModel(order);
        OrderVm orderVm = OrderVm.fromModel(order, Set.of(item));
        OrderGetVm orderGetVm = OrderGetVm.fromModel(order, Set.of(item));
        List<OrderItemGetVm> orderItemGetVms = OrderItemGetVm.fromModels(Set.of(item));

        assertThat(addressVm.phone()).isEqualTo("0909");
        assertThat(briefVm.id()).isEqualTo(order.getId());
        assertThat(orderVm.orderItemVms()).hasSize(1);
        assertThat(orderGetVm.id()).isEqualTo(order.getId());
        assertThat(orderItemGetVms).hasSize(1);
        assertThat(OrderItemGetVm.fromModels(List.of())).isEmpty();
    }

    @Test
    void simpleRecords_shouldConstructSuccessfully() {
        OrderListVm listVm = OrderListVm.builder()
                .orderList(List.of())
                .totalElements(0)
                .totalPages(0)
                .build();
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "detail");
        ResponeStatusVm statusVm = new ResponeStatusVm("title", "message", "200");
        ProductVariationVm variationVm = new ProductVariationVm(1L, "name", "sku");
        ProductQuantityItem quantityItem = ProductQuantityItem.builder().productId(1L).quantity(2L).build();

        assertNotNull(listVm);
        assertThat(errorVm.fieldErrors()).isNotNull();
        assertThat(statusVm.statusCode()).isEqualTo("200");
        assertThat(variationVm.id()).isEqualTo(1L);
        assertThat(quantityItem.quantity()).isEqualTo(2L);
        assertThat(Constants.ErrorCode.ORDER_NOT_FOUND).isEqualTo("ORDER_NOT_FOUND");
        assertThat(Constants.MessageCode.CREATE_CHECKOUT).contains("Create checkout");
        assertThat(Constants.Column.ORDER_ORDER_STATUS_COLUMN).isEqualTo("orderStatus");
    }

    private static Order createOrder() {
        Order order = Order.builder()
                .id(100L)
                .email("alice@example.com")
                .shippingAddressId(createAddress(1L))
                .billingAddressId(createAddress(2L))
                .note("note")
                .tax(1f)
                .discount(0.5f)
                .numberItem(1)
                .totalPrice(new BigDecimal("99.99"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("CODE")
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .checkoutId("checkout-1")
                .build();
        order.setCreatedOn(ZonedDateTime.now());
        return order;
    }

    private static OrderAddress createAddress(Long id) {
        return OrderAddress.builder()
                .id(id)
                .contactName("name")
                .phone("0909")
                .addressLine1("line1")
                .addressLine2("line2")
                .city("city")
                .zipCode("70000")
                .districtId(1L)
                .districtName("district")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("state")
                .countryId(3L)
                .countryName("country")
                .build();
    }

    private static OrderItem createOrderItem(Long orderId) {
        return OrderItem.builder()
                .id(1L)
                .orderId(orderId)
                .productId(10L)
                .productName("Product")
                .quantity(2)
                .productPrice(new BigDecimal("20.00"))
                .discountAmount(new BigDecimal("1.00"))
                .taxAmount(new BigDecimal("2.00"))
                .taxPercent(new BigDecimal("10.00"))
                .build();
    }
}
