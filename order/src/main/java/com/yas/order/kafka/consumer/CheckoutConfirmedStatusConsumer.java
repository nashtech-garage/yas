package com.yas.order.kafka.consumer;

import static com.yas.order.kafka.helper.ConsumerHelper.processForEventUpdate;
import static com.yas.order.utils.JsonUtils.getJsonValueOrNull;
import static com.yas.order.utils.JsonUtils.getJsonValueOrThrow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.service.CheckoutItemService;
import com.yas.order.service.CheckoutService;
import com.yas.order.service.OrderAddressService;
import com.yas.order.service.OrderItemService;
import com.yas.order.service.OrderService;
import com.yas.order.utils.Constants;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  After the Checkout Status is set to PAYMENT_CONFIRMED, an order will be created.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutConfirmedStatusConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutConfirmedStatusConsumer.class);
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final CheckoutService checkoutService;
    private final CheckoutItemService checkoutItemService;
    private final OrderAddressService orderAddressService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${cdc.event.checkout.status.topic-name}",
        groupId = "${cdc.event.checkout.confirmed.status.group-id}"
    )
    @RetryableTopic
    public void listen(ConsumerRecord<?, ?> consumerRecord) {
        processForEventUpdate(
            consumerRecord,
            this::handleJsonForUpdateCheckout,
            objectMapper,
            LOGGER
        );
    }

    private void handleJsonForUpdateCheckout(JsonNode valueObject) {

        JsonNode before = valueObject.get("before");
        JsonNode after = valueObject.get("after");

        String id = getJsonValueOrThrow(after, Constants.Column.ID_COLUMN, Constants.ErrorCode.ID_NOT_EXISTED);
        String beforeStatus = getJsonValueOrNull(before, Constants.Column.STATUS_COLUMN);
        String afterStatus = getJsonValueOrNull(after, Constants.Column.STATUS_COLUMN);

        if (Objects.isNull(afterStatus)
            || afterStatus.equals(beforeStatus)
            || !CheckoutState.PAYMENT_CONFIRMED.name().equals(afterStatus)
        ) {
            LOGGER.info("It's not an event to create Order with Checkout Id {}", id);
            return;
        }

        LOGGER.info("Checkout record with ID {} has the status 'PAYMENT_CONFIRMED'", id);

        Checkout checkout = checkoutService.findCheckoutById(id);
        List<CheckoutItem> checkoutItemList = checkoutItemService.getAllByCheckoutId(checkout.getId());

        Order order = createOrder(checkout, checkoutItemList);
        createOrderItems(order, checkoutItemList);
        updateCheckoutStatus(checkout);
    }

    private Order createOrder(Checkout checkout, List<CheckoutItem> checkoutItemList) {

        Order order = Order.builder()
            .email(checkout.getEmail())
            .numberItem(checkoutItemList.size())
            .note(checkout.getNote())
            .tax(checkout.getTotalTax())
            .discount(checkout.getTotalDiscountAmount())
            .totalPrice(checkout.getTotalAmount())
            .couponCode(checkout.getPromotionCode())
            .orderStatus(OrderStatus.PAYMENT_CONFIRMED)
            .deliveryFee(checkout.getTotalShipmentFee())
            .deliveryMethod(checkout.getShipmentMethodId())
            .deliveryStatus(DeliveryStatus.PREPARING)
            .totalShipmentTax(checkout.getTotalShipmentTax())
            .customerId(checkout.getCustomerId())
            .shippingAddressId(
                Optional.ofNullable(checkout.getShippingAddressId())
                    .map(orderAddressService::findOrderAddressById)
                    .orElseThrow(() -> new BadRequestException("Shipping Address Id is not existed: {}",
                        checkout.getShippingAddressId()))
            )
            .billingAddressId(
                Optional.ofNullable(checkout.getBillingAddressId())
                    .map(orderAddressService::findOrderAddressById)
                    .orElseThrow(() -> new BadRequestException("Billing Address Id is not existed: {}",
                        checkout.getBillingAddressId()))
            )
            .checkoutId(checkout.getId())
            .build();

        return orderService.updateOrder(order);

    }

    private void createOrderItems(Order order, List<CheckoutItem> checkoutItemList) {

        List<OrderItem> orderItems = checkoutItemList.stream()
            .map(item -> OrderItem.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .productPrice(item.getProductPrice())
                .orderId(order.getId())
                .build())
            .toList();

        orderItemService.saveAll(orderItems);
    }

    private void updateCheckoutStatus(Checkout checkout) {
        checkout.setCheckoutState(CheckoutState.FULFILLED);
        checkoutService.updateCheckout(checkout);
    }

}
