package com.yas.order.service;

import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.OrderPostVm;
import com.yas.order.viewmodel.OrderVm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.Validate;

import java.util.List;
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
        Validate.notNull(orderPostVm, "Order cannot be null");
        Validate.notNull(orderPostVm.orderItemPostVms(), "Order Items cannot be null");

//        TO-DO: handle check inventory when inventory is complete
//        ************

//        TO-DO: handle payment
//        ************

        Order order = Order.builder()
                .phone(orderPostVm.phone())
                .address(orderPostVm.address())
                .note(orderPostVm.note())
                .tax(orderPostVm.tax())
                .discount(orderPostVm.discount())
                .numberItem(orderPostVm.numberItem())
                .totalPrice(orderPostVm.totalPrice())
                .deliveryFee(orderPostVm.deliveryFee())
                .deliveryMethod(orderPostVm.deliveryMethod())
                .deliveryStatus(orderPostVm.deliveryStatus())
                .paymentMethod(orderPostVm.paymentMethod())
                .build();
        orderRepository.save(order);

        List<OrderItem> orderItems = orderPostVm.orderItemPostVms().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.productId())
                        .quantity(item.quantity())
                        .price(item.price())
                        .note(item.note())
                        .orderId(order)
                        .build())
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);

//        TO-DO: decrement inventory when inventory is complete
//        ************

        return OrderVm.fromModel(order);
    }
}
