package com.yas.order.service;

import static com.yas.order.utils.Constants.ErrorCode.ADDRESS_NOT_AVAILABLE;
import static com.yas.order.utils.Constants.ErrorCode.CHECKOUT_IS_EXPIRED;
import static com.yas.order.utils.Constants.ErrorCode.CHECKOUT_NOT_FOUND;
import static com.yas.order.utils.Constants.ErrorCode.PAYMENT_NOT_AVAILABLE;

import com.yas.commonlibrary.constants.ApiConstant;
import com.yas.commonlibrary.constants.MessageCode;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.ForbiddenException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.AddressMapper;
import com.yas.order.mapper.CheckoutMapper;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutAddress;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutAddressRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.repository.OrderAddressRepository;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPatchVm;
import com.yas.order.viewmodel.checkout.CheckoutPaymentMethodPutVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import com.yas.order.viewmodel.customer.ActiveAddressVm;
import com.yas.order.viewmodel.enumeration.CheckoutAddressType;
import com.yas.order.viewmodel.payment.PaymentProviderVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.promotion.DiscountType;
import com.yas.order.viewmodel.promotion.PromotionVerifyResultDto;
import com.yas.order.viewmodel.promotion.PromotionVerifyVm;
import com.yas.order.viewmodel.tax.TaxRateVm;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final CheckoutAddressRepository checkoutAddressRepository;
    private final CheckoutRepository checkoutRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final PromotionService promotionService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final ShipmentProviderService shipmentProviderService;
    private final TaxService taxService;
    private final CheckoutMapper checkoutMapper;
    private final AddressMapper addressMapper;

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
                .shippingAddressDetail(addressMapper.toVm(checkout.getCheckoutAddress()))
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
                    .taxClassId(product.getTaxClassId())
                    .height(product.getHeight())
                    .width(product.getWidth())
                    .length(product.getLength())
                    .weight(product.getWeight())
                    .dimensionUnit(product.getDimensionUnit())
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
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, id));
        checkout.setPaymentMethodId(checkoutPaymentMethodPutVm.paymentMethodId());
        log.info(Constants.MessageCode.UPDATE_CHECKOUT_PAYMENT,
                checkout.getId(),
                checkoutPaymentMethodPutVm.paymentMethodId(),
                checkout.getPaymentMethodId()
        );
        checkoutRepository.save(checkout);
    }

    private boolean isNotOwnedByCurrentUser(Checkout checkout) {
        return !checkout.getCreatedBy().equals(AuthenticationUtils.extractUserId());
    }

    public CheckoutVm updateCheckoutByFields(String id, CheckoutPatchVm checkoutPatchVm) {
        Checkout existingCheckout = checkoutRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, id));

        if (isNotOwnedByCurrentUser(existingCheckout)) {
            throw new ForbiddenException(ApiConstant.FORBIDDEN, "You are not authorized to update this checkout");
        }

        validateExpiredCheckout(existingCheckout);
        updateAndValidateCheckout(existingCheckout, checkoutPatchVm);
        reCalculateCheckoutAmount(existingCheckout);

        CheckoutVm checkoutVm = checkoutMapper.toVm(checkoutRepository.save(existingCheckout));
        Set<CheckoutItemVm> checkoutItemVms = existingCheckout.getCheckoutItems()
            .stream()
            .map(checkoutMapper::toVm)
            .collect(Collectors.toSet());
        return checkoutVm.toBuilder()
            .checkoutItemVms(checkoutItemVms)
            .shippingAddressDetail(addressMapper.toVm(existingCheckout.getCheckoutAddress()))
            .build();
    }

    private void validateExpiredCheckout(Checkout existingCheckout) {
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(existingCheckout.getCreatedOn(), now);
        if (duration.toSeconds() > 3600) {
            throw new BadRequestException(CHECKOUT_IS_EXPIRED);
        }
    }

    private void updateAndValidateCheckout(Checkout existingCheckout, CheckoutPatchVm checkoutPatchVm) {
        if (checkoutPatchVm.paymentMethodId() != null) {
            updatePaymentMethod(existingCheckout, checkoutPatchVm.paymentMethodId());
        }
        if (checkoutPatchVm.shippingAddressId() != null) {
            updateAddress(existingCheckout, checkoutPatchVm.shippingAddressId());
        }
        if (checkoutPatchVm.shipmentMethodId() != null || existingCheckout.getShipmentMethodId() != null) {
            String value = checkoutPatchVm.shipmentMethodId() != null
                ? checkoutPatchVm.shipmentMethodId()
                : existingCheckout.getShipmentMethodId();
            updateShipmentProvider(existingCheckout, value);
        }
        if (checkoutPatchVm.promotionCode() != null || existingCheckout.getPromotionCode() != null) {
            String promoCode = checkoutPatchVm.promotionCode() != null
                ? checkoutPatchVm.promotionCode()
                : existingCheckout.getPromotionCode();
            updatePromotionCode(existingCheckout, promoCode);
        }
    }

    public void updatePaymentMethod(Checkout existingCheckout, String value) {
        Optional<PaymentProviderVm> paymentProvider = paymentService.getPaymentProviders()
            .stream().filter(payment -> Objects.equals(payment.id(), value))
            .findFirst();
        if (paymentProvider.isEmpty()) {
            throw new NotFoundException(PAYMENT_NOT_AVAILABLE, value);
        }
        existingCheckout.setPaymentMethodId(value);
    }

    public void updateAddress(Checkout existingCheckout, String value) {
        Optional<ActiveAddressVm> address = customerService.getUserAddresses()
            .stream().filter(add -> Objects.equals(add.id(), Long.valueOf(value)))
            .findFirst();
        if (address.isEmpty()) {
            throw new NotFoundException(ADDRESS_NOT_AVAILABLE, value);
        }

        existingCheckout.setShippingAddressId(Long.parseLong(value));

        CheckoutAddress existedCheckoutAddress = checkoutAddressRepository
            .findByCheckoutIdAndType(existingCheckout.getId(), CheckoutAddressType.SHIPPING)
            .map(checkoutAddress -> addressMapper.updateModel(checkoutAddress, address.get()))
            .orElseGet(() -> {
                CheckoutAddress checkoutAddress = addressMapper.toModel(address.get());
                checkoutAddress.setCheckout(existingCheckout);
                return checkoutAddress;
            });

        existedCheckoutAddress.setType(CheckoutAddressType.SHIPPING);
        existingCheckout.setCheckoutAddress(existedCheckoutAddress);

        List<Long> taxClassIds = existingCheckout.getCheckoutItems()
            .stream()
            .map(CheckoutItem::getTaxClassId)
            .collect(Collectors.toSet())
            .stream().toList();

        updateTax(existingCheckout, taxClassIds);
    }

    private void updateTax(Checkout existingCheckout, List<Long> taxClassId) {
        List<TaxRateVm> taxRateVmList = taxService.getTaxRate(taxClassId,
            existingCheckout.getCheckoutAddress().getCountryId(),
            existingCheckout.getCheckoutAddress().getStateOrProvinceId(),
            existingCheckout.getCheckoutAddress().getZipCode());

        existingCheckout.getCheckoutItems().forEach(item ->
            taxRateVmList.forEach(tax -> {
                if (Objects.equals(item.getTaxClassId(), tax.taxClassId())) {
                    item.setTaxAmount(calculateTaxAmount(tax.rate(), item.getProductPrice()));
                }
            })
        );
    }

    // This function is using mock data
    public void updateShipmentProvider(Checkout existingCheckout, String value) {
        if (shipmentProviderService.checkShipmentProviderAvailable(value)) {
            existingCheckout.setShipmentMethodId(value);
        }
        if (existingCheckout.getShippingAddressId() != null) {
            // Mock data
            existingCheckout.getCheckoutItems().forEach(item -> {
                item.setShipmentFee(new BigDecimal(5000));
                item.setShipmentTax(new BigDecimal(500));
            });
        }
    }

    public void updatePromotionCode(Checkout existingCheckout, String value) {
        if (Objects.equals(value, "")) {
            existingCheckout.getCheckoutItems().forEach(item -> item.setDiscountAmount(BigDecimal.ZERO));
            existingCheckout.setPromotionCode(null);
            return;
        }

        List<Long> productIds = existingCheckout.getCheckoutItems()
            .stream()
            .map(CheckoutItem::getProductId)
            .collect(Collectors.toSet())
            .stream().toList();

        PromotionVerifyVm promotionVerifyVm = PromotionVerifyVm
            .builder()
            .orderPrice(existingCheckout.getTotalAmount().longValue())
            .couponCode(value)
            .productIds(productIds)
            .build();

        PromotionVerifyResultDto promotion = promotionService.validateCouponCode(promotionVerifyVm);

        existingCheckout.getCheckoutItems().forEach(item -> {
            if (Objects.equals(item.getProductId(), promotion.productId())) {
                BigDecimal discount = DiscountType.FIXED.equals(promotion.discountType())
                    ? BigDecimal.valueOf(promotion.discountValue())
                    : calculateDiscount(promotion.discountValue(), item.getProductPrice());
                item.setDiscountAmount(discount);
                existingCheckout.setPromotionCode(value);
            }
        });
    }

    private void reCalculateCheckoutAmount(Checkout existingCheckout) {
        BigDecimal totalShipmentFee = BigDecimal.ZERO;
        BigDecimal totalShipmentTax = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        BigDecimal totalProductAmount = BigDecimal.ZERO;

        for (CheckoutItem item : existingCheckout.getCheckoutItems()) {
            int quantity = item.getQuantity();
            BigDecimal quantityAsBigDecimal = BigDecimal.valueOf(quantity);

            totalShipmentFee = totalShipmentFee.add(safeValue(item.getShipmentFee()));
            totalShipmentTax = totalShipmentTax.add(safeValue(item.getShipmentTax()));
            totalTax = totalTax.add(safeValue(item.getTaxAmount()).multiply(quantityAsBigDecimal));
            totalDiscountAmount = totalDiscountAmount.add(safeValue(item.getDiscountAmount()).multiply(quantityAsBigDecimal));
            totalProductAmount = totalProductAmount.add(safeValue(item.getProductPrice()).multiply(quantityAsBigDecimal));
        }

        BigDecimal totalAmount = totalProductAmount
            .add(totalShipmentFee)
            .add(totalShipmentTax)
            .add(totalTax)
            .subtract(totalDiscountAmount);

        existingCheckout.setTotalShipmentFee(totalShipmentFee);
        existingCheckout.setTotalShipmentTax(totalShipmentTax);
        existingCheckout.setTotalTax(totalTax);
        existingCheckout.setTotalDiscountAmount(totalDiscountAmount);
        existingCheckout.setTotalAmount(totalAmount);
    }

    public BigDecimal calculateDiscount(Long discountValue, BigDecimal productPrice) {
        BigDecimal valueDecimal = BigDecimal.valueOf(discountValue);
        BigDecimal discountPercentage = BigDecimal.valueOf(100);

        return valueDecimal
            .multiply(productPrice)
            .divide(discountPercentage, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTaxAmount(Double taxRate, BigDecimal productPrice) {
        BigDecimal taxRateDecimal = BigDecimal.valueOf(taxRate);

        return taxRateDecimal
            .multiply(productPrice)
            .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    private BigDecimal safeValue(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}