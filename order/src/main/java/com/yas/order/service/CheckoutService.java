package com.yas.order.service;

import static com.yas.order.utils.Constants.ErrorCode.CHECKOUT_NOT_FOUND;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.constants.ApiConstant;
import com.yas.commonlibrary.constants.MessageCode;
import com.yas.commonlibrary.exception.ForbiddenException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.CheckoutMapper;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPaymentMethodPutVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
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
    private final OrderService orderService;
    private final ProductService productService;
    private final CheckoutMapper checkoutMapper;

    public void processPayment(String checkoutId) {
        Checkout checkout = checkoutRepository.findById(checkoutId)
            .orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, checkoutId));
        checkout.setCheckoutState(CheckoutState.PAYMENT_PROCESSING);
        checkout.setProgress(CheckoutProgress.STOCK_LOCKED);
        checkout.setPaymentMethodId(PaymentMethod.PAYPAL);
        checkout.setShippingAddressId(1L);
        checkout.setBillingAddressId(1L);
        checkoutRepository.save(checkout);
    }

    /**
     * Creates a new {@link Checkout} object in a PENDING state.
     *
     * @param checkoutPostVm the view model containing checkout details and items
     * @return a {@link CheckoutVm} object representing the newly created checkout
     */
    public CheckoutVm createCheckout(CheckoutPostVm checkoutPostVm) {
        Checkout checkout = checkoutMapper.toModel(checkoutPostVm);
        checkout.setCheckoutState(CheckoutState.PENDING);
        checkout.setCustomerId(AuthenticationUtils.extractUserId());

        prepareCheckoutItems(checkout, checkoutPostVm);
        checkout = checkoutRepository.save(checkout);

        CheckoutVm checkoutVm = checkoutMapper.toVm(checkout);
        Set<CheckoutItemVm> checkoutItemVms = checkout.getCheckoutItems()
                .stream()
                .map(checkoutMapper::toVm)
                .collect(Collectors.toSet());
        log.info(Constants.MessageCode.CREATE_CHECKOUT, checkout.getId(), checkout.getCustomerId());
        return checkoutVm.toBuilder()
                .checkoutItemVms(checkoutItemVms)
                .build();
    }

    private void prepareCheckoutItems(Checkout checkout, CheckoutPostVm checkoutPostVm) {
        Set<Long> productIds = checkoutPostVm.checkoutItemPostVms()
                .stream()
                .map(CheckoutItemPostVm::productId)
                .collect(Collectors.toSet());

        List<CheckoutItem> checkoutItems = checkoutPostVm.checkoutItemPostVms()
                .stream()
                .map(checkoutMapper::toModel)
                .map(item -> {
                    item.setCheckout(checkout);
                    return item;
                }).toList();

        Map<Long, ProductCheckoutListVm> products
                = productService.getProductInfomation(productIds, 0, productIds.size());

        List<CheckoutItem> enrichedItems = enrichCheckoutItemsWithProductDetails(products, checkoutItems);
        BigDecimal totalAmount = enrichedItems.stream()
                .map(item -> item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        checkout.setCheckoutItems(enrichedItems);
        checkout.setTotalAmount(totalAmount);
    }

    private List<CheckoutItem> enrichCheckoutItemsWithProductDetails(
            Map<Long, ProductCheckoutListVm> products,
            List<CheckoutItem> checkoutItems) {
        return checkoutItems.stream().map(item -> {
            ProductCheckoutListVm product = products.get(item.getProductId());
            if (product == null) {
                throw new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, item.getProductId());
            }
            return item.toBuilder()
                    .productName(product.getName())
                    .productPrice(BigDecimal.valueOf(product.getPrice()))
                    .build();
        }).toList();
    }

    public CheckoutVm getCheckoutPendingStateWithItemsById(String id) {
        Checkout checkout = checkoutRepository.findByIdAndCheckoutState(id, CheckoutState.PENDING).orElseThrow(()
                -> new NotFoundException(CHECKOUT_NOT_FOUND, id));

        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException(ApiConstant.FORBIDDEN, "You can not view this checkout");
        }

        CheckoutVm checkoutVm = checkoutMapper.toVm(checkout);

        List<CheckoutItem> checkoutItems = checkout.getCheckoutItems();
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

        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException(ApiConstant.FORBIDDEN, "You are not authorized to update this checkout");
        }

        checkout.setCheckoutState(CheckoutState.valueOf(checkoutStatusPutVm.checkoutStatus()));
        checkoutRepository.save(checkout);
        log.info(Constants.MessageCode.UPDATE_CHECKOUT_STATUS,
                checkout.getId(),
                checkoutStatusPutVm.checkoutStatus(),
                checkout.getCheckoutState()
        );
        Order order = orderService.findOrderByCheckoutId(checkoutStatusPutVm.checkoutId());
        return order.getId();
    }

    public void updateCheckoutPaymentMethod(String id, CheckoutPaymentMethodPutVm checkoutPaymentMethodPutVm) {

        if (Objects.isNull(checkoutPaymentMethodPutVm.paymentMethodId())) {
            return;
        }

        Checkout checkout = checkoutRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, id));
        checkout.setPaymentMethodId(PaymentMethod.fromValue(checkoutPaymentMethodPutVm.paymentMethodId()));
        checkoutRepository.save(checkout);
    }

    public Checkout findCheckoutById(String id) {

        return this.checkoutRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, id));
    }

    public CheckoutVm findCheckoutWithItemsById(String id) {

        Checkout checkout =  findCheckoutById(id);

        List<CheckoutItem> checkoutItems = checkout.getCheckoutItems();

        Set<CheckoutItemVm> checkoutItemVms = Optional.ofNullable(checkoutItems)
            .orElse(Collections.emptyList())
            .stream()
            .map(checkoutMapper::toVm)
            .collect(Collectors.toSet());

        return CheckoutVm.fromModel(checkout, checkoutItemVms);
    }

    public void updateCheckout(Checkout checkout) {

        if (Objects.isNull(checkout.getId())) {
            throw new BadRequestException(Constants.ErrorCode.ID_NOT_EXISTED);
        }
        checkoutRepository.save(checkout);
    }

    private boolean isNotOwnedByCurrentUser(Checkout checkout) {
        return !checkout.getCreatedBy().equals(AuthenticationUtils.extractUserId());
    }
}