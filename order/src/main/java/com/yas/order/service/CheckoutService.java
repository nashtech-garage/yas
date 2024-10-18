package com.yas.order.service;

import static com.yas.order.utils.Constants.ErrorCode.CHECKOUT_NOT_FOUND;

import com.yas.commonlibrary.exception.Forbidden;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.CheckoutMapper;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutItemRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final CheckoutItemRepository checkoutItemRepository;
    private final OrderService orderService;
    private final CheckoutMapper checkoutMapper;

    public CheckoutVm createCheckout(CheckoutPostVm checkoutPostVm) {

        Checkout checkout = checkoutMapper.toModel(checkoutPostVm);
        checkout.setCheckoutState(CheckoutState.PENDING);
        checkout.setId(UUID.randomUUID().toString());

        Checkout savedCheckout = checkoutRepository.save(checkout);

        CheckoutVm checkoutVm = checkoutMapper.toVm(savedCheckout);
        if (CollectionUtils.isEmpty(checkoutPostVm.checkoutItemPostVms())) {
            return checkoutVm;
        }

        List<CheckoutItem> checkoutItemList = checkoutPostVm.checkoutItemPostVms()
            .stream()
            .map(checkoutItemPostVm -> {
                CheckoutItem item = checkoutMapper.toModel(checkoutItemPostVm);
                item.setCheckoutId(savedCheckout.getId());
                return item;
            })
            .toList();

        List<CheckoutItem> savedCheckoutItems = checkoutItemRepository.saveAll(checkoutItemList);

        Set<CheckoutItemVm> checkoutItemVms = savedCheckoutItems
            .stream()
            .map(checkoutMapper::toVm)
            .collect(Collectors.toSet());

        return checkoutVm.toBuilder().checkoutItemVms(checkoutItemVms).build();
    }

    public CheckoutVm getCheckoutPendingStateWithItemsById(String id) {

        Checkout checkout = checkoutRepository.findByIdAndCheckoutState(id, CheckoutState.PENDING).orElseThrow(()
                -> new NotFoundException(CHECKOUT_NOT_FOUND, id));

        if (!checkout.getCreatedBy().equals(AuthenticationUtils.getCurrentUserId())) {
            throw new Forbidden(Constants.ErrorCode.FORBIDDEN);
        }

        CheckoutVm checkoutVm = checkoutMapper.toVm(checkout);

        List<CheckoutItem> checkoutItems = checkoutItemRepository.findAllByCheckoutId(checkout.getId());
        if (CollectionUtils.isEmpty(checkoutItems)) {
            return checkoutVm;
        }

        Set<CheckoutItemVm> checkoutItemVms = checkoutItems
            .stream()
            .map(checkoutMapper::toVm)
            .collect(Collectors.toSet());

        return checkoutVm.toBuilder().checkoutItemVms(checkoutItemVms).build();
    }

    public Long updateCheckoutStatus(CheckoutStatusPutVm checkoutStatusPutVm) {
        Checkout checkout = checkoutRepository.findById(checkoutStatusPutVm.checkoutId())
                .orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, checkoutStatusPutVm.checkoutId()));
        checkout.setCheckoutState(CheckoutState.valueOf(checkoutStatusPutVm.checkoutStatus()));
        checkoutRepository.save(checkout);
        Order order = orderService.findOrderByCheckoutId(checkoutStatusPutVm.checkoutId());
        return order.getId();
    }
}
