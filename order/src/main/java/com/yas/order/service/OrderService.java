package com.yas.order.service;

import com.yas.order.exception.NotFoundException;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.EOrderStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.OrderPostVm;
import com.yas.order.viewmodel.OrderVm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }


    public OrderVm createOrder(OrderPostVm orderPostVm) {
//        TO-DO: handle check inventory when inventory is complete
//        ************

//        TO-DO: handle payment
//        ************

        Order order = Order.builder()
                .phone(orderPostVm.phone())
                .email(orderPostVm.email())
                .shippingAddressId(orderPostVm.shippingAddressId())
                .billingAddressId(orderPostVm.billingAddressId())
                .note(orderPostVm.note())
                .tax(orderPostVm.tax())
                .discount(orderPostVm.discount())
                .numberItem(orderPostVm.numberItem())
                .totalPrice(orderPostVm.totalPrice())
                .couponCode(orderPostVm.couponCode())
                .orderStatus(EOrderStatus.PENDING)
                .deliveryFee(orderPostVm.deliveryFee())
                .deliveryMethod(orderPostVm.deliveryMethod())
                .deliveryStatus(orderPostVm.deliveryStatus())
                .paymentMethod(orderPostVm.paymentMethod())
                .paymentStatus(orderPostVm.paymentStatus())
                .build();
        orderRepository.save(order);

        Set<OrderItem> orderItems = orderPostVm.orderItemPostVms().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.productId())
                        .quantity(item.quantity())
                        .productPrice(item.productPrice())
                        .note(item.note())
                        .orderId(order)
                        .build())
                .collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItems);

        //setOrderItems so that we able to return order with orderItems
        order.setOrderItems(orderItems);

 //        TO-DO: delete Item in Cart
//        ************

//        TO-DO: decrement inventory when inventory is complete
//        ************

        return OrderVm.fromModel(order);
    }

    public OrderVm getOrderWithItemsById(long id) {
        Order order = orderRepository.findById(id).orElseThrow(()
                -> new NotFoundException(Constants.ERROR_CODE.ORDER_NOT_FOUND, id));

        return OrderVm.fromModel(order);
    }
}
