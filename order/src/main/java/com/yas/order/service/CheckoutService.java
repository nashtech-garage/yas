package com.yas.order.service;

import com.yas.order.exception.Forbidden;
import com.yas.order.exception.NotFoundException;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.enumeration.ECheckoutState;
import com.yas.order.repository.CheckoutItemRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final CheckoutItemRepository checkoutItemRepository;

    public CheckoutVm createCheckout(CheckoutPostVm checkoutPostVm) {
        try {
            //check if already exist checkout then return, no need to create new one
             List<CheckoutItemPostVm> checkoutItems = checkoutPostVm.checkoutItemPostVms();

List<CheckoutItemPostVm> uniqueItems = checkoutItems.stream()
        .distinct()
        .collect(Collectors.toList());

List<Long> productIds = uniqueItems.stream()
        .map(CheckoutItemPostVm::productId)
        .collect(Collectors.toList());

List<Integer> itemQuantities = uniqueItems.stream()
        .map(CheckoutItemPostVm::quantity)
        .collect(Collectors.toList());

            Optional<Checkout> checkoutOptional = checkoutRepository
                    .findByCheckoutByUserIdAndProductIdsAndQuantites(AuthenticationUtils.getCurrentUserId(), productIds, itemQuantities, (long) productIds.size());
            if (checkoutOptional.isPresent()) {
                return CheckoutVm.fromModel(checkoutOptional.get());
            }
        } catch (Exception ex) {
                 log.error("create Checkout fail: " + ex);
        }

        UUID uuid = UUID.randomUUID();
        Checkout checkout = Checkout.builder()
                .id(uuid.toString())
                .email(checkoutPostVm.email())
                .note(checkoutPostVm.note())
                .couponCode(checkoutPostVm.couponCode())
                .checkoutState(ECheckoutState.PENDING)
                .build();
        checkoutRepository.save(checkout);

        Set<CheckoutItem> checkoutItems = checkoutPostVm.checkoutItemPostVms().stream()
                .map(item -> CheckoutItem.builder()
                        .productId(item.productId())
                        .productName(item.productName())
                        .quantity(item.quantity())
                        .productPrice(item.productPrice())
                        .note(item.note())
                        .discountAmount(item.discountAmount())
                        .taxPercent(item.taxPercent())
                        .taxAmount(item.taxAmount())
                        .checkoutId(checkout)
                        .build())
                .collect(Collectors.toSet());
        checkoutItemRepository.saveAll(checkoutItems);

        //setCheckoutItem so that we able to return checkout with checkoutItems
        checkout.setCheckoutItem(checkoutItems);
        return CheckoutVm.fromModel(checkout);
    }

    public CheckoutVm getCheckoutWithItemsById(String id) {
        Checkout checkout = checkoutRepository.findById(id).orElseThrow(()
                -> new NotFoundException(Constants.ERROR_CODE.CHECKOUT_NOT_FOUND, id));

        if (!checkout.getCreatedBy().equals(AuthenticationUtils.getCurrentUserId()))
            throw new Forbidden(Constants.ERROR_CODE.FORBIDDEN);
        return CheckoutVm.fromModel(checkout);
    }
}
