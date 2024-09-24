package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.order.OrderApplication;
import com.yas.order.config.IntegrationTestConfiguration;
import com.yas.order.exception.NotFoundException;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = OrderApplication.class)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceIT {

    @MockBean
    private ProductService productService;

    @MockBean
    private CartService cartService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    private OrderItemPostVm orderItemPostVm;

    private OrderAddressPostVm orderAddressPostVm;

    private OrderPostVm orderPostVm;

    @BeforeEach
    void setUp() {
        orderItemPostVm = OrderItemPostVm.builder()
                .productId(1L).productName("abc")
                .quantity(1).productPrice(BigDecimal.TEN)
                .discountAmount(BigDecimal.ONE).taxAmount(BigDecimal.ONE).taxPercent(BigDecimal.ONE)
                .build();

        orderAddressPostVm = OrderAddressPostVm.builder()
                .contactName("contactName").phone("phone").addressLine1("addressLine1").addressLine2("addressLine2")
                .city("city").zipCode("zipCode").districtId(1L).districtName("districtName")
                .stateOrProvinceId(1L).stateOrProvinceName("stateOrProvinceName")
                .countryId(1L).countryName("countryName")
                .build();

        orderPostVm = OrderPostVm.builder()
                .checkoutId("1")
                .email("abc@gmail.com")
                .orderItemPostVms(Arrays.asList(orderItemPostVm))
                .billingAddressPostVm(orderAddressPostVm)
                .shippingAddressPostVm(orderAddressPostVm)
                .build();
    }

    @AfterEach
    void tearDown() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void testCreateOrder_successful() {

        OrderVm orderVm = orderService.createOrder(orderPostVm);

        Optional<Order> orderOptional = orderRepository.findById(orderVm.id());
        assertTrue(orderOptional.isPresent());
        Order orderDb = orderOptional.get();
        assertEquals("abc@gmail.com", orderDb.getEmail());
        assertEquals(1, orderDb.getOrderItems().size());
        assertEquals("abc", orderDb.getOrderItems().stream().findFirst().get().getProductName());
    }

    @Test
    void testGetOrderWithItemsById_whenNormalCase_returnOrderVm() {
        orderService.createOrder(orderPostVm);
        List<Order> orders = orderRepository.findAll();
        OrderVm order = orderService.getOrderWithItemsById(orders.getFirst().getId());
        assertNotNull(order);
        assertEquals("abc@gmail.com", order.email());
    }

    @Test
    void testGetOrderWithItemsById_whenNotFound_throwNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class,
            () -> orderService.getOrderWithItemsById(23L));
        assertEquals("Order 23 is not found", exception.getMessage());
    }

    @Test
    void testGetAllOrder_whenNormalCase_returnOrderListVm() {

        orderService.createOrder(orderPostVm);

        ZonedDateTime createdFrom = ZonedDateTime.now().minusDays(7);
        ZonedDateTime createdTo = ZonedDateTime.now().plusDays(1);
        String warehouse = "";
        String productName = "abc";
        List<OrderStatus> orderStatus = List.of(OrderStatus.ACCEPTED);
        String billingCountry = "";
        String billingPhoneNumber = "";
        String email = "abc@gmail.com";
        int pageNo = 0;
        int pageSize = 10;

        OrderListVm orderListVm = orderService.getAllOrder(
            createdFrom,
            createdTo,
            warehouse,
            productName,
            orderStatus,
            billingCountry,
            billingPhoneNumber,
            email,
            pageNo,
            pageSize
        );
        assertNotNull(orderListVm.orderList());

    }

    @Test
    void testGetAllOrder_whenOrderPageIsEmpty_returnOrderListVm() {

        ZonedDateTime createdFrom = ZonedDateTime.now().minusDays(7);
        ZonedDateTime createdTo = ZonedDateTime.now().plusDays(1);
        String warehouse = "e3";
        String productName = "abc2";
        List<OrderStatus> orderStatus = List.of(OrderStatus.ACCEPTED);
        String billingCountry = "e3";
        String billingPhoneNumber = "3e";
        String email = "ab2c@gmail.com";
        int pageNo = 0;
        int pageSize = 10;

        OrderListVm orderListVm = orderService.getAllOrder(
            createdFrom,
            createdTo,
            warehouse,
            productName,
            orderStatus,
            billingCountry,
            billingPhoneNumber,
            email,
            pageNo,
            pageSize
        );

        assertNull(orderListVm.orderList());
    }

    @Test
    void testFindOrderByCheckoutId_whenNormalCase_returnOrder() {
        orderService.createOrder(orderPostVm);
        Order order = orderService.findOrderByCheckoutId("1");
        assertNotNull(order);
        assertEquals("abc@gmail.com", order.getEmail());
    }

    @Test
    void testFindOrderByCheckoutId_whenNotFound_throwNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class,
            () -> orderService.findOrderByCheckoutId("23"));
        assertEquals("Order of checkoutId 23 is not found", exception.getMessage());
    }

    @Test
    void testUpdateOrderPaymentStatus_whenPaymentStatusNotCompleted_returnPaymentOrderStatusVm() {

        orderService.createOrder(orderPostVm);
        List<Order> orders = orderRepository.findAll();
        Order order = orders.getFirst();
        PaymentOrderStatusVm paymentOrderStatusVm = new PaymentOrderStatusVm(
            order.getId(), OrderStatus.ACCEPTED.getName(), 1L, PaymentStatus.PENDING.name()
        );

        PaymentOrderStatusVm actual = orderService.updateOrderPaymentStatus(paymentOrderStatusVm);
        assertEquals(OrderStatus.ACCEPTED.getName(), actual.orderStatus());
    }

    @Test
    void testUpdateOrderPaymentStatus_whenNormalCase_returnPaymentOrderStatusVm() {

        orderService.createOrder(orderPostVm);
        List<Order> orders = orderRepository.findAll();
        Order order = orders.getFirst();
        PaymentOrderStatusVm paymentOrderStatusVm = new PaymentOrderStatusVm(
            order.getId(), OrderStatus.ACCEPTED.getName(), 1L, PaymentStatus.COMPLETED.name()
        );

        PaymentOrderStatusVm actual = orderService.updateOrderPaymentStatus(paymentOrderStatusVm);
        assertEquals(OrderStatus.PAID.getName(), actual.orderStatus());
    }

    @Test
    void testUpdateOrderPaymentStatus_whenNotFound_throwNotFoundException() {

        PaymentOrderStatusVm paymentOrderStatusVm = new PaymentOrderStatusVm(
            1L, OrderStatus.ACCEPTED.getName(), 1L, PaymentStatus.COMPLETED.name()
        );

        Exception exception = assertThrows(NotFoundException.class,
            () -> orderService.updateOrderPaymentStatus(paymentOrderStatusVm));
        assertEquals("Order 1 is not found", exception.getMessage());
    }

    @Test
    void testRejectOrder_whenNormalCase_saveOrder() {

        orderService.createOrder(orderPostVm);
        List<Order> orders = orderRepository.findAll();
        Order order = orders.getFirst();
        orderService.rejectOrder(order.getId(), "test reason");

        Optional<Order> actual = orderRepository.findById(order.getId());
        assertNotNull(actual);
        if (actual.isPresent()) {
            assertEquals(OrderStatus.REJECT, actual.get().getOrderStatus());
            assertEquals("test reason", actual.get().getRejectReason());
        }

    }

    @Test
    void testRejectOrder_whenNotFound_throwNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class,
            () -> orderService.rejectOrder(1L, "test reason 2"));
        assertEquals("Order 1 is not found", exception.getMessage());
    }


    @Test
    void testAcceptOrder_whenNormalCase_saveOrder() {
        orderService.createOrder(orderPostVm);
        List<Order> orders = orderRepository.findAll();
        Order order = orders.getFirst();
        orderService.acceptOrder(order.getId());

        Optional<Order> actual = orderRepository.findById(order.getId());
        assertNotNull(actual);
        actual.ifPresent(value -> assertEquals(OrderStatus.ACCEPTED, value.getOrderStatus()));
    }

    @Test
    void testAcceptOrder_whenNotFound_throwNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class,
            () -> orderService.acceptOrder(2L));
        assertEquals("Order 2 is not found", exception.getMessage());
    }


}
