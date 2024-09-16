package com.yas.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.order.OrderApplication;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.service.OrderService;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrderController.class)
@ContextConfiguration(classes = OrderApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreateOrder_whenRequestIsValid_thenReturnOrderVm() throws Exception {

        OrderPostVm request = getOrderPostVm();
        OrderVm response = getOrderVm();
        when(orderService.createOrder(request)).thenReturn(response);

        mockMvc.perform(post("/storefront/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(response)));
    }

    @Test
    void testUpdateOrderPaymentStatus_whenRequestIsValid_thenReturnPaymentOrderStatusVm()
        throws Exception {

        PaymentOrderStatusVm request = new PaymentOrderStatusVm(
            1001L,
            "Completed",
            5001L,
            "Paid"
        );

        PaymentOrderStatusVm response = new PaymentOrderStatusVm(
            12345L,
            "Shipped",
            67890L,
            "Completed"
        );

        when(orderService.updateOrderPaymentStatus(request)).thenReturn(response);

        mockMvc.perform(put("/storefront/orders/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(response)));
    }

    @Test
    void testCheckOrder_whenRequestIsValid_thenReturnOrderExistsByProductAndUserGetVm() throws Exception {

        Long productId = 1L;
        OrderExistsByProductAndUserGetVm orderExistsByProductAndUserGetVm
            = new OrderExistsByProductAndUserGetVm(false);
        when(orderService.isOrderCompletedWithUserIdAndProductId(productId))
            .thenReturn(orderExistsByProductAndUserGetVm);

        mockMvc.perform(get("/storefront/orders/completed").param("productId", productId.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                .json(objectWriter.writeValueAsString(orderExistsByProductAndUserGetVm)));
    }

    @Test
    void testGetMyOrders_whenRequestIsValid_thenReturnOrderGetVm() throws Exception {

        String productName = "test-name";
        OrderStatus orderStatus = OrderStatus.COMPLETED;

        OrderGetVm order1 = new OrderGetVm(
            1L,
            OrderStatus.COMPLETED,
            new BigDecimal("100.00"),
            DeliveryStatus.CANCELLED,
            DeliveryMethod.GRAB_EXPRESS,
            List.of(),
            null
        );
        OrderGetVm order2 = new OrderGetVm(
            2L,
            OrderStatus.COMPLETED,
            new BigDecimal("150.00"),
            DeliveryStatus.DELIVERED,
            DeliveryMethod.GRAB_EXPRESS,
            List.of(),
            null
        );

        when(orderService.getMyOrders(productName, orderStatus))
            .thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/storefront/orders/my-orders")
                .param("productName", productName)
                .param("orderStatus", orderStatus.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                .json(objectWriter.writeValueAsString(List.of(order1, order2))));
    }

    @Test
    void testGetOrderWithItemsById_whenRequestIsValid_thenReturnOrderVm() throws Exception {

        long productId = 1L;
        OrderVm orderVm = getOrderVm();
        when(orderService.getOrderWithItemsById(productId))
            .thenReturn(orderVm);

        mockMvc.perform(get("/backoffice/orders/{id}", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                .json(objectWriter.writeValueAsString(orderVm)));
    }

    @Test
    void testGetOrders_whenRequestIsValid_thenReturnOrderListVm() throws Exception {

        OrderListVm orderListVm = new OrderListVm(
            null,
            2L,
            1
        );
        when(orderService.getAllOrder(
            any(),
            any(),
            anyString(),
            anyString(),
            anyList(),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            anyInt()
        )).thenReturn(orderListVm);

        mockMvc.perform(get("/backoffice/orders")
                .param("createdFrom", "1970-01-01T00:00:00Z")
                .param("createdTo", ZonedDateTime.now().toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                .json(objectWriter.writeValueAsString(orderListVm)));
    }

    private OrderVm getOrderVm() {

        OrderAddressVm shippingAddress = new OrderAddressVm(
            1L,
            "John Doe",
            "+1234567890",
            "123 Elm Street",
            "Apt 3B",
            "Springfield",
            "62704",
            10L,
            "Downtown",
            20L,
            "Illinois",
            30L,
            "United States"
        );

        OrderAddressVm billingAddress = new OrderAddressVm(
            1L,
            "Jane Smith",
            "+1987654321",
            "789 Pine Street",
            "Suite 202",
            "Metropolis",
            "12345",
            102L,
            "North District",
            202L,
            "California",
            302L,
            "United States"
        );

        Set<OrderItemVm> items = getOrderItemVms();

        return new OrderVm(
            1001L,
            "alice.johnson@example.com",
            shippingAddress,
            billingAddress,
            "Please deliver by next week.",
            7.50f,
            15.00f,
            3,
            new BigDecimal("159.97"),
            new BigDecimal("7.99"),
            "WINTER2024",
            OrderStatus.COMPLETED,
            DeliveryMethod.GRAB_EXPRESS,
            DeliveryStatus.PREPARING,
            PaymentStatus.COMPLETED,
            items
        );
    }

    private Set<OrderItemVm> getOrderItemVms() {
        OrderItemVm item1 = new OrderItemVm(
            1L,
            101L,
            "Smartphone",
            2,
            new BigDecimal("299.99"),
            "Latest model with extended warranty",
            new BigDecimal("20.00"),
            new BigDecimal("24.00"),
            new BigDecimal("8.00"),
            1001L
        );

        OrderItemVm item2 = new OrderItemVm(
            12L,
            102L,
            "Smartphone 2",
            2,
            new BigDecimal("299.99"),
            "Latest model with extended warranty",
            new BigDecimal("20.00"),
            new BigDecimal("24.00"),
            new BigDecimal("8.00"),
            1001L
        );

        Set<OrderItemVm> items = new HashSet<>();
        items.add(item1);
        items.add(item2);
        return items;
    }

    private OrderPostVm getOrderPostVm() {

        OrderAddressPostVm shippingAddress = new OrderAddressPostVm(
            "John Doe",
            "+123456789",
            "123 Main St",
            "Apt 4B",
            "Springfield",
            "62701",
            101L,
            "Downtown",
            201L,
            "Illinois",
            301L,
            "USA"
        );

        OrderAddressPostVm billingAddress = new OrderAddressPostVm(
            "Jane Smith",
            "+1987654321",
            "789 Elm Street",
            "Suite 5A",
            "Greenville",
            "29601",
            102L,
            "North District",
            202L,
            "South Carolina",
            302L,
            "United States"
        );

        List<OrderItemPostVm> items = getOrderItemPostVms();

        return new OrderPostVm(
            "checkoutId123",
            "customer@example.com",
            shippingAddress,
            billingAddress,
            "Please handle with care.",
            5.00f,
            10.00f,
            2,
            new BigDecimal("89.97"),
            new BigDecimal("5.00"),
            "COUPON2024",
            DeliveryMethod.YAS_EXPRESS,
            PaymentMethod.BANKING,
            PaymentStatus.COMPLETED,
            items
        );
    }

    private List<OrderItemPostVm> getOrderItemPostVms() {
        OrderItemPostVm item1 = new OrderItemPostVm(
            123L,
            "Wireless Mouse",
            2,
            new BigDecimal("25.99"),
            "Includes batteries",
            new BigDecimal("5.00"),
            new BigDecimal("2.00"),
            new BigDecimal("8.00")
        );

        OrderItemPostVm item2 = new OrderItemPostVm(
            1234L,
            "Wireless Mouse 2",
            3,
            new BigDecimal("25.99"),
            "Includes batteries",
            new BigDecimal("5.00"),
            new BigDecimal("2.00"),
            new BigDecimal("8.00")
        );

        return List.of(item1, item2);
    }

}