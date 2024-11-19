package com.yas.order.service;

import static com.yas.order.utils.Constants.ErrorCode.ORDER_NOT_FOUND;

import com.yas.commonlibrary.csv.BaseCsv;
import com.yas.commonlibrary.csv.CsvExporter;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.specification.OrderSpecification;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final PromotionService promotionService;

    public OrderVm createOrder(OrderPostVm orderPostVm) {

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
                .orderStatus(OrderStatus.PENDING)
                .deliveryFee(orderPostVm.deliveryFee())
                .deliveryMethod(orderPostVm.deliveryMethod())
                .deliveryStatus(DeliveryStatus.PREPARING)
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
                        .orderId(order.getId())
                        .build())
                .collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItems);

        OrderVm orderVm = OrderVm.fromModel(order, orderItems);
        productService.subtractProductStockQuantity(orderVm);
        cartService.deleteCartItems(orderVm);
        acceptOrder(orderVm.id());

        // update promotion
        List<PromotionUsageVm> promotionUsageVms = new ArrayList<>();
        orderItems.forEach(item -> {
            PromotionUsageVm promotionUsageVm = PromotionUsageVm.builder()
                    .productId(item.getProductId())
                    .orderId(order.getId())
                    .promotionCode(order.getCouponCode())
                    .build();
            promotionUsageVms.add(promotionUsageVm);
        });
        promotionService.updateUsagePromotion(promotionUsageVms);
        return orderVm;
    }

    public OrderVm getOrderWithItemsById(long id) {

        Order order = orderRepository.findById(id).orElseThrow(()
                -> new NotFoundException(Constants.ErrorCode.ORDER_NOT_FOUND, id));

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
        return OrderVm.fromModel(order, new HashSet<>(orderItems));
    }

    public OrderListVm getAllOrder(Pair<ZonedDateTime, ZonedDateTime> timePair,
                                   String productName,
                                   List<OrderStatus> orderStatus,
                                   Pair<String, String> billingPair,
                                   String email,
                                   Pair<Integer, Integer> infoPage) {

        Sort sort = Sort.by(Sort.Direction.DESC, Constants.Column.CREATE_ON_COLUMN);
        Pageable pageable = PageRequest.of(infoPage.getFirst(), infoPage.getSecond(), sort);

        List<OrderStatus> allOrderStatus = Arrays.asList(OrderStatus.values());

        ZonedDateTime createdFrom = timePair.getFirst();
        ZonedDateTime createdTo = timePair.getSecond();
        String billingCountry = billingPair.getFirst();
        String billingPhoneNumber = billingPair.getSecond();

        Specification<Order> spec = OrderSpecification.findOrderByWithMulCriteria(
            orderStatus.isEmpty() ? allOrderStatus : orderStatus,
            billingPhoneNumber,
            billingCountry,
            email,
            productName,
            createdFrom,
            createdTo
        );

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        if (orderPage.isEmpty()) {
            return new OrderListVm(null, 0, 0);
        }

        List<OrderBriefVm> orderVms = orderPage.getContent()
                .stream()
                .map(OrderBriefVm::fromModel)
                .toList();

        return new OrderListVm(orderVms, orderPage.getTotalElements(), orderPage.getTotalPages());
    }

    public List<OrderBriefVm> getLatestOrders(int count) {

        if (count <= 0) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, count);
        List<Order> orders = orderRepository.getLatestOrders(pageable);

        if (CollectionUtils.isEmpty(orders)) {
            return List.of();
        }

        return orders.stream()
                .map(OrderBriefVm::fromModel)
                .toList();
    }

    public OrderExistsByProductAndUserGetVm isOrderCompletedWithUserIdAndProductId(final Long productId) {

        String userId = AuthenticationUtils.getCurrentUserId();

        List<ProductVariationVm> productVariations = productService.getProductVariations(productId);

        List<Long> productIds;
        if (CollectionUtils.isEmpty(productVariations)) {
            productIds = Collections.singletonList(productId);
        } else {
            productIds = productVariations.stream().map(ProductVariationVm::id).toList();
        }

        Specification<Order>
            spec = OrderSpecification.existsByCreatedByAndInProductIdAndOrderStatusCompleted(userId, productIds);
        boolean existedOrder = orderRepository.findOne(spec).isPresent();

        return new OrderExistsByProductAndUserGetVm(existedOrder);
    }

    public List<OrderGetVm> getMyOrders(String productName, OrderStatus orderStatus) {
        String userId = AuthenticationUtils.getCurrentUserId();
        Specification<Order> orderSpec = OrderSpecification.findMyOrders(userId, productName, orderStatus);
        Sort sort = Sort.by(Sort.Direction.DESC, Constants.Column.CREATE_ON_COLUMN);
        List<Order> orders = orderRepository.findAll(orderSpec, sort);
        return orders.stream().map(order -> OrderGetVm.fromModel(order, null)).toList();
    }

    public OrderGetVm findOrderVmByCheckoutId(String checkoutId) {
        Order order = this.findOrderByCheckoutId(checkoutId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
        return OrderGetVm.fromModel(order, new HashSet<>(orderItems));
    }

    public Order findOrderByCheckoutId(String checkoutId) {
        return this.orderRepository.findByCheckoutId(checkoutId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, "of checkoutId " + checkoutId));
    }

    public PaymentOrderStatusVm updateOrderPaymentStatus(PaymentOrderStatusVm paymentOrderStatusVm) {
        var order = this.orderRepository
                .findById(paymentOrderStatusVm.orderId())
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, paymentOrderStatusVm.orderId()));

        order.setPaymentId(paymentOrderStatusVm.paymentId());
        String paymentStatus = paymentOrderStatusVm.paymentStatus();
        order.setPaymentStatus(PaymentStatus.valueOf(paymentStatus));
        if (PaymentStatus.COMPLETED.name().equals(paymentStatus)) {
            order.setOrderStatus(OrderStatus.PAID);
        }
        Order result = this.orderRepository.save(order);
        return PaymentOrderStatusVm.builder()
                .orderId(result.getId())
                .orderStatus(result.getOrderStatus().getName())
                .paymentId(paymentOrderStatusVm.paymentId())
                .paymentStatus(paymentOrderStatusVm.paymentStatus())
                .build();
    }

    public void rejectOrder(Long orderId, String rejectReason) {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, orderId));
        order.setOrderStatus(OrderStatus.REJECT);
        order.setRejectReason(rejectReason);
        this.orderRepository.save(order);
    }

    public void acceptOrder(Long orderId) {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, orderId));
        order.setOrderStatus(OrderStatus.ACCEPTED);
        this.orderRepository.save(order);
    }

    public Order updateOrder(Order order) {
        if (Objects.isNull(order)) {
            throw new BadRequestException("Order is not existed.");
        }
        return orderRepository.save(order);
    }

    public byte[] exportCsv(OrderRequest orderRequest) throws IOException {
        ZonedDateTime createdFrom = orderRequest.getCreatedFrom();
        ZonedDateTime createdTo = orderRequest.getCreatedTo();
        String productName = orderRequest.getProductName();
        List<OrderStatus> orderStatus = orderRequest.getOrderStatus();
        String billingCountry = orderRequest.getBillingCountry();
        String billingPhoneNumber = orderRequest.getBillingPhoneNumber();
        String email = orderRequest.getEmail();
        int pageNo = orderRequest.getPageNo();
        int pageSize = orderRequest.getPageSize();

        OrderListVm orderListVm = getAllOrder(
            Pair.of(createdFrom, createdTo),
            productName,
            orderStatus,
            Pair.of(billingCountry, billingPhoneNumber),
            email,
            Pair.of(pageNo, pageSize)
        );

        if (Objects.isNull(orderListVm.orderList())) {
            return CsvExporter.exportToCsv(List.of(), OrderItemCsv.class);
        }

        List<BaseCsv> orders = orderListVm.orderList().stream().map(orderMapper::toCsv).collect(
            Collectors.toUnmodifiableList());
        return CsvExporter.exportToCsv(orders, OrderItemCsv.class);
    }
}
