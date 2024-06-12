package com.yas.order.controller;

import com.yas.order.model.enumeration.EOrderStatus;
import com.yas.order.service.OrderService;
import com.yas.order.viewmodel.order.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/storefront/orders")
    public ResponseEntity<OrderVm> createOrder(@Valid @RequestBody OrderPostVm orderPostVm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        OrderVm orderVm = orderService.createOrder(orderPostVm);
        return ResponseEntity.ok(orderVm);
    }

    @PutMapping("/storefront/orders/status")
    public ResponseEntity<PaymentOrderStatusVm> updateOrderPaymentStatus(@Valid @RequestBody PaymentOrderStatusVm paymentOrderStatusVm) {
        PaymentOrderStatusVm orderStatusVm = orderService.updateOrderPaymentStatus(paymentOrderStatusVm);
        return ResponseEntity.ok(orderStatusVm);
    }

    @GetMapping("/storefront/orders/completed")
    public ResponseEntity<OrderExistsByProductAndUserGetVm> checkOrderExistsByProductIdAndUserIdWithStatus(
            @RequestParam Long productId) {
        return ResponseEntity.ok(orderService.isOrderCompletedWithUserIdAndProductId(productId));
    }

    @GetMapping("/storefront/orders/my-orders")
    public ResponseEntity<List<OrderGetVm>> getMyOrders(@RequestParam String productName,
                                                        @RequestParam(required = false) EOrderStatus orderStatus) {
        return ResponseEntity.ok(orderService.getMyOrders(productName, orderStatus));
    }

    @GetMapping("/backoffice/orders/{id}")
    public ResponseEntity<OrderVm> getOrderWithItemsById(@PathVariable long id) {
        return ResponseEntity.ok(orderService.getOrderWithItemsById(id));
    }

    @GetMapping("/backoffice/orders")
    public ResponseEntity<OrderListVm> getOrders(
            @RequestParam(value = "createdFrom", defaultValue = "#{new java.util.Date(1970-01-01)}", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime createdFrom,
            @RequestParam(value = "createdTo", defaultValue = "#{new java.util.Date()}", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime createdTo,
            @RequestParam(value = "warehouse", defaultValue = "", required = false) String warehouse,
            @RequestParam(value = "productName", defaultValue = "", required = false) String productName,
            @RequestParam(value = "orderStatus", defaultValue = "", required = false) List<EOrderStatus> orderStatus,
            @RequestParam(value = "billingPhoneNumber", defaultValue = "", required = false) String billingPhoneNumber,
            @RequestParam(value = "email", defaultValue = "", required = false) String email,
            @RequestParam(value = "billingCountry", defaultValue = "", required = false) String billingCountry,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(orderService.getAllOrder(
                createdFrom,
                createdTo,
                warehouse,
                productName,
                orderStatus,
                billingCountry,
                billingPhoneNumber,
                email,
                pageNo,
                pageSize));
    }
}
