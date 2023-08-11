package com.yas.order.service;

import com.yas.order.exception.NotFoundException;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EOrderStatus;
import com.yas.order.model.enumeration.EPaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.order.*;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVM;
import com.yas.saga.order.command.PaymentStatusCommand;
import com.yas.saga.order.command.UpdateOrderPaymentStatusCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yas.order.utils.Constants.ERROR_CODE.ORDER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    public OrderVm createOrder(OrderPostVm orderPostVm) {
//        TO-DO: handle check inventory when inventory is complete
//        ************

//        TO-DO: handle payment
//        ************

        OrderAddressPostVm billingAddressPostVm = orderPostVm.billingAddressPostVm();
        OrderAddress billOrderAddress = OrderAddress.builder()
                .phone(billingAddressPostVm.phone())
                .contactName(billingAddressPostVm.contactName())
                .addressLine1(billingAddressPostVm.addressLine1())
                .addressLine2(billingAddressPostVm.addressLine2())
                .city(billingAddressPostVm.city())
                .zipCode(billingAddressPostVm.zipCode())
                .districtId(billingAddressPostVm.districtId())
                .districtName(billingAddressPostVm.districtName())
                .stateOrProvinceId(billingAddressPostVm.stateOrProvinceId())
                .stateOrProvinceName(billingAddressPostVm.stateOrProvinceName())
                .countryId(billingAddressPostVm.countryId())
                .countryName(billingAddressPostVm.countryName())
                .build();

        OrderAddressPostVm shipOrderAddressPostVm = orderPostVm.shippingAddressPostVm();
        OrderAddress shippOrderAddress = OrderAddress.builder()
                .phone(shipOrderAddressPostVm.phone())
                .contactName(shipOrderAddressPostVm.contactName())
                .addressLine1(shipOrderAddressPostVm.addressLine1())
                .addressLine2(shipOrderAddressPostVm.addressLine2())
                .city(shipOrderAddressPostVm.city())
                .zipCode(shipOrderAddressPostVm.zipCode())
                .districtId(shipOrderAddressPostVm.districtId())
                .districtName(shipOrderAddressPostVm.districtName())
                .stateOrProvinceId(shipOrderAddressPostVm.stateOrProvinceId())
                .stateOrProvinceName(shipOrderAddressPostVm.stateOrProvinceName())
                .countryId(shipOrderAddressPostVm.countryId())
                .countryName(shipOrderAddressPostVm.countryName())
                .build();

        Order order = Order.builder()
                .email(orderPostVm.email())
                .note(orderPostVm.note())
                .tax(orderPostVm.tax())
                .discount(orderPostVm.discount())
                .numberItem(orderPostVm.numberItem())
                .totalPrice(orderPostVm.totalPrice())
                .couponCode(orderPostVm.couponCode())
                .orderStatus(EOrderStatus.PENDING_PAYMENT)
                .deliveryFee(orderPostVm.deliveryFee())
                .deliveryMethod(orderPostVm.deliveryMethod())
                .deliveryStatus(EDeliveryStatus.PREPARING)
                .paymentStatus(orderPostVm.paymentStatus())
                .shippingAddressId(shippOrderAddress)
                .billingAddressId(billOrderAddress)
                .checkoutId(orderPostVm.checkoutId())
                .build();
        orderRepository.save(order);


        Set<OrderItem> orderItems = orderPostVm.orderItemPostVms().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.productId())
                        .productName(item.productName())
                        .quantity(item.quantity())
                        .productPrice(item.productPrice())
                        .note(item.note())
                        .orderId(order)
                        .build())
                .collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItems);

        //setOrderItems so that we able to return order with orderItems
        order.setOrderItems(orderItems);

        log.info("Order Success: " + order);
        return OrderVm.fromModel(order);
    }

    public OrderVm getOrderWithItemsById(long id) {
        Order order = orderRepository.findById(id).orElseThrow(()
                -> new NotFoundException(Constants.ERROR_CODE.ORDER_NOT_FOUND, id));

        return OrderVm.fromModel(order);
    }

    public OrderListVm getAllOrder(ZonedDateTime createdFrom,
                                   ZonedDateTime createdTo,
                                   String warehouse,
                                   String productName,
                                   List<EOrderStatus> orderStatus,
                                   String billingCountry,
                                   String billingPhoneNumber,
                                   String email,
                                   int pageNo,
                                   int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        List<EOrderStatus> allOrderStatus = Arrays.asList(EOrderStatus.values());
        Page<Order> orderPage = orderRepository.findOrderByWithMulCriteria(
                orderStatus.isEmpty() ? allOrderStatus : orderStatus,
                billingPhoneNumber,
                billingCountry,
                email.toLowerCase(),
                productName.toLowerCase(),
                createdFrom,
                createdTo,
                pageable);
        if(orderPage.isEmpty())
            return new OrderListVm(null, 0, 0);

        List<OrderBriefVm> orderVms = orderPage.getContent()
                .stream()
                .map(OrderBriefVm::fromModel)
                .collect(Collectors.toList());

        return new OrderListVm(orderVms, orderPage.getTotalElements(), orderPage.getTotalPages());
    }

    public OrderExistsByProductAndUserGetVm isOrderCompletedWithUserIdAndProductId(final Long productId) {

        String userId = AuthenticationUtils.getCurrentUserId();

        List<ProductVariationVM> productVariations = productService.getProductVariations(productId);

        List<Long> productIds;
        if (CollectionUtils.isEmpty(productVariations)) {
            productIds = Collections.singletonList(productId);
        } else {
            productIds = productVariations.stream().map(ProductVariationVM::id).toList();
        }

        return new OrderExistsByProductAndUserGetVm(
                orderRepository.existsByCreatedByAndInProductIdAndOrderStatusCompleted(userId, productIds)
        );
    }

    public List<OrderGetVm> getMyOrders(String productName, EOrderStatus orderStatus) {
        String userId = AuthenticationUtils.getCurrentUserId();
        List<Order> orders = orderRepository.findMyOrders(userId, productName, orderStatus);
        return orders.stream().map(OrderGetVm::fromModel).toList();
    }

    public Order findOrderByCheckoutId(String checkoutId) {
        return this.orderRepository.findByCheckoutId(checkoutId)
            .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, "of checkoutId " + checkoutId));
    }

    public Order updateOrderPaymentStatus(UpdateOrderPaymentStatusCommand command) {
        var order = this.orderRepository
            .findById(command.orderId())
            .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, command.orderId()));

        order.setPaymentId(command.paymentId());
        PaymentStatusCommand paymentStatus = command.paymentStatus();
        order.setPaymentStatus(EPaymentStatus.valueOf(paymentStatus.name()));
        if (PaymentStatusCommand.COMPLETED.equals(paymentStatus)) {
            order.setOrderStatus(EOrderStatus.PAID);
        }
        return this.orderRepository.save(order);
    }
}
