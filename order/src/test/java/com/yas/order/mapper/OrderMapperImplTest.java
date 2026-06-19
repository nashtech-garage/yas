package com.yas.order.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderMapperImplTest {

    private OrderMapperImpl orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapperImpl();
    }

    @Test
    void toCsv_WhenOrderBriefVmIsNull_ShouldReturnNull() {
        assertNull(orderMapper.toCsv(null));
    }

    @Test
    void toCsv_WhenBillingAddressIsNull_ShouldReturnCsvWithNullPhone() {
        OrderBriefVm mockOrder = mock(OrderBriefVm.class);
        
        when(mockOrder.billingAddressVm()).thenReturn(null); 
        
        when(mockOrder.id()).thenReturn(1L);
        when(mockOrder.email()).thenReturn("test@test.com");
        when(mockOrder.totalPrice()).thenReturn(new BigDecimal("100.0"));
        when(mockOrder.orderStatus()).thenReturn(OrderStatus.PENDING);
        when(mockOrder.paymentStatus()).thenReturn(PaymentStatus.PENDING);
        when(mockOrder.deliveryStatus()).thenReturn(DeliveryStatus.PREPARING);
        when(mockOrder.createdOn()).thenReturn(ZonedDateTime.now());

        OrderItemCsv result = orderMapper.toCsv(mockOrder);

        assertNotNull(result);
        assertNull(result.getPhone());
        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getEmail());
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
    }

    @Test
    void toCsv_WhenBillingAddressIsNotNull_ShouldReturnCsvWithPhone() {
        OrderAddressVm mockAddress = mock(OrderAddressVm.class);
        when(mockAddress.phone()).thenReturn("0123456789");

        OrderBriefVm mockOrder = mock(OrderBriefVm.class);
        
        when(mockOrder.billingAddressVm()).thenReturn(mockAddress);
        
        when(mockOrder.id()).thenReturn(2L);
        when(mockOrder.email()).thenReturn("user@test.com");
        when(mockOrder.totalPrice()).thenReturn(new BigDecimal("200.0"));
        when(mockOrder.orderStatus()).thenReturn(OrderStatus.COMPLETED);
        when(mockOrder.paymentStatus()).thenReturn(PaymentStatus.COMPLETED);
        when(mockOrder.deliveryStatus()).thenReturn(DeliveryStatus.DELIVERED);
        when(mockOrder.createdOn()).thenReturn(ZonedDateTime.now());

        OrderItemCsv result = orderMapper.toCsv(mockOrder);

        assertNotNull(result);
        assertEquals("0123456789", result.getPhone());
        assertEquals(2L, result.getId());
        assertEquals("user@test.com", result.getEmail());
        assertEquals(OrderStatus.COMPLETED, result.getOrderStatus());
    }
}