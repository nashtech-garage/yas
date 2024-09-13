package com.yas.order.service;

import static com.yas.order.utils.Constants.ErrorCode.CHECKOUT_NOT_FOUND;

import com.yas.order.exception.Forbidden;
import com.yas.order.exception.NotFoundException;
import com.yas.order.mapper.CheckoutMapper;
import com.yas.order.model.Checkout;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final OrderService orderService;
    private final CheckoutMapper checkoutMapper;

    public CheckoutVm createCheckout(CheckoutPostVm checkoutPostVm) {
        Checkout checkout = checkoutMapper.toModel(checkoutPostVm);
        checkout.setCheckoutState(CheckoutState.PENDING);
        checkout.setId(UUID.randomUUID().toString());
        checkout.getCheckoutItem().forEach(item -> item.setCheckoutId(checkout));
        return checkoutMapper.toVm(checkoutRepository.save(checkout));
    }

    public CheckoutVm getCheckoutPendingStateWithItemsById(String id) {
        Checkout checkout = checkoutRepository.findByIdAndCheckoutState(id, CheckoutState.PENDING).orElseThrow(()
                -> new NotFoundException(CHECKOUT_NOT_FOUND, id));

        if (!checkout.getCreatedBy().equals(AuthenticationUtils.getCurrentUserId())) {
            throw new Forbidden(Constants.ErrorCode.FORBIDDEN);
        }
        return checkoutMapper.toVm(checkout);
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
