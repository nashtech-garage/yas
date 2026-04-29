package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderCsvAndRequestTest {

    @Test
    void testOrderItemCsvBuilder() {
        ZonedDateTime now = ZonedDateTime.now();
        OrderItemCsv csv = OrderItemCsv.builder()
                .id(1L)
                .orderStatus(OrderStatus.COMPLETED)
                .paymentStatus(PaymentStatus.COMPLETED)
                .email("test@example.com")
                .phone("0123456789")
                .totalPrice(BigDecimal.valueOf(100))
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .createdOn(now)
                .build();

        assertEquals(1L, csv.getId());
        assertEquals(OrderStatus.COMPLETED, csv.getOrderStatus());
        assertEquals(PaymentStatus.COMPLETED, csv.getPaymentStatus());
        assertEquals("test@example.com", csv.getEmail());
        assertEquals("0123456789", csv.getPhone());
        assertEquals(BigDecimal.valueOf(100), csv.getTotalPrice());
        assertEquals(DeliveryStatus.DELIVERED, csv.getDeliveryStatus());
        assertEquals(now, csv.getCreatedOn());
    }

    @Test
    void testOrderItemCsvSetters() {
        OrderItemCsv csv = OrderItemCsv.builder().build();
        csv.setOrderStatus(OrderStatus.PENDING);
        csv.setEmail("new@email.com");
        csv.setPhone("999");
        csv.setTotalPrice(BigDecimal.ONE);
        csv.setDeliveryStatus(DeliveryStatus.PREPARING);
        csv.setPaymentStatus(PaymentStatus.PENDING);
        csv.setCreatedOn(ZonedDateTime.now());

        assertEquals(OrderStatus.PENDING, csv.getOrderStatus());
        assertEquals("new@email.com", csv.getEmail());
    }

    @Test
    void testOrderRequestBuilderAndGetters() {
        ZonedDateTime from = ZonedDateTime.now().minusDays(7);
        ZonedDateTime to = ZonedDateTime.now();

        OrderRequest request = OrderRequest.builder()
                .createdFrom(from).createdTo(to).warehouse("WH1")
                .productName("Product X").orderStatus(List.of(OrderStatus.PENDING))
                .billingPhoneNumber("123").email("test@test.com")
                .billingCountry("VN").pageNo(0).pageSize(10).build();

        assertEquals(from, request.getCreatedFrom());
        assertEquals(to, request.getCreatedTo());
        assertEquals("WH1", request.getWarehouse());
        assertEquals("Product X", request.getProductName());
        assertEquals(1, request.getOrderStatus().size());
        assertEquals("123", request.getBillingPhoneNumber());
        assertEquals("test@test.com", request.getEmail());
        assertEquals("VN", request.getBillingCountry());
        assertEquals(0, request.getPageNo());
        assertEquals(10, request.getPageSize());
    }

    @Test
    void testOrderRequestSetters() {
        OrderRequest request = new OrderRequest();
        request.setCreatedFrom(ZonedDateTime.now());
        request.setCreatedTo(ZonedDateTime.now());
        request.setWarehouse("WH2");
        request.setProductName("Test");
        request.setOrderStatus(List.of(OrderStatus.COMPLETED));
        request.setBillingPhoneNumber("456");
        request.setEmail("e@e.com");
        request.setBillingCountry("US");
        request.setPageNo(1);
        request.setPageSize(20);

        assertEquals("WH2", request.getWarehouse());
        assertEquals("Test", request.getProductName());
        assertEquals(1, request.getPageNo());
        assertEquals(20, request.getPageSize());
    }

    @Test
    void testOrderRequestAllArgsConstructor() {
        ZonedDateTime now = ZonedDateTime.now();
        OrderRequest request = new OrderRequest(now, now, "WH", "prod",
                List.of(OrderStatus.PENDING), "phone", "email", "country", 0, 10);
        assertNotNull(request);
        assertEquals("WH", request.getWarehouse());
    }
}
