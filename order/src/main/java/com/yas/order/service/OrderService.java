package com.yas.order.service;

import com.yas.order.exception.NotFoundException;
import com.yas.order.model.Checkout;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.ECheckoutState;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EOrderStatus;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.order.*;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final CheckoutRepository checkoutRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
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
                .orderStatus(EOrderStatus.PENDING)
                .deliveryFee(orderPostVm.deliveryFee())
                .deliveryMethod(orderPostVm.deliveryMethod())
                .deliveryStatus(EDeliveryStatus.PREPARING)
                .paymentStatus(orderPostVm.paymentStatus())
                .shippingAddressId(shippOrderAddress)
                .billingAddressId(billOrderAddress)
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

        // delete Item in Cart
        try {
            cartService.deleteCartItemByProductId(orderItems.stream().map(i -> i.getProductId()).toList());
        } catch (Exception ex) {
            log.error("Delete products in cart fail: " + ex.getMessage());
        }

//        TO-DO: decrement inventory when inventory is complete
//        ************

        checkoutRepository.findById(orderPostVm.checkoutId())
                .ifPresent(checkout -> {
                    checkout.setCheckoutState(ECheckoutState.COMPLETED);
                    checkoutRepository.save(checkout);
                    log.info("Update checkout state: " + checkout);
                });

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
}
