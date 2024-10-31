package com.yas.promotion.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.PromotionUsage;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.repository.PromotionUsageRepository;
import com.yas.promotion.utils.AuthenticationUtils;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    private final ProductService productService;

    public PromotionDetailVm createPromotion(PromotionPostVm promotionPostVm) {
        validateNewPromotion(promotionPostVm);

        Promotion promotion = Promotion.builder()
                .name(promotionPostVm.getName())
                .slug(promotionPostVm.getSlug())
                .description(promotionPostVm.getDescription())
                .couponCode(promotionPostVm.getCouponCode())
                .applyTo(promotionPostVm.getApplyTo())
                .usageType(promotionPostVm.getUsageType())
                .usageLimit(promotionPostVm.getUsageLimit())
                .discountType(promotionPostVm.getDiscountType())
                .discountPercentage(promotionPostVm.getDiscountPercentage())
                .discountAmount(promotionPostVm.getDiscountAmount())
                .isActive(promotionPostVm.isActive())
                .startDate(promotionPostVm.getStartDate().toInstant())
                .endDate(promotionPostVm.getEndDate().toInstant())
                .minimumOrderPurchaseAmount(promotionPostVm.getMinimumOrderPurchaseAmount())
                .build();

        List<PromotionApply> promotionApplies =
                PromotionPostVm.createPromotionApplies(promotionPostVm, promotion);
        promotion.setPromotionApplies(promotionApplies);

        return PromotionDetailVm.fromModel(promotionRepository.save(promotion));
    }

    private void validateNewPromotion(PromotionPostVm promotionPostVm) {
        validateIfPromotionExistedSlug(promotionPostVm.getSlug());
        validateIfCouponCodeIsExisted(promotionPostVm.getCouponCode());
        validateIfPromotionEndDateIsBeforeStartDate(
                promotionPostVm.getStartDate().toInstant(),
                promotionPostVm.getEndDate().toInstant());
    }

    public PromotionDetailVm updatePromotion(PromotionPutVm promotionPutVm) {
        Optional<Promotion> promotionOp = promotionRepository.findById(promotionPutVm.getId());

        if (promotionOp.isEmpty()) {
            throw new NotFoundException(Constants.ErrorCode.PROMOTION_NOT_FOUND_ERROR_MESSAGE, promotionPutVm.getId());
        }

        Promotion promotion = promotionOp.get();

        promotion.setApplyTo(promotionPutVm.getApplyTo());
        promotion.setName(promotionPutVm.getName());
        promotion.setDescription(promotionPutVm.getDescription());
        promotion.setCouponCode(promotionPutVm.getCouponCode());
        promotion.setUsageType(promotionPutVm.getUsageType());
        promotion.setUsageLimit(promotionPutVm.getUsageLimit());
        promotion.setSlug(promotionPutVm.getSlug());
        promotion.setDiscountType(promotionPutVm.getDiscountType());
        promotion.setDiscountPercentage(promotionPutVm.getDiscountPercentage());
        promotion.setDiscountAmount(promotionPutVm.getDiscountAmount());
        promotion.setIsActive(promotionPutVm.getIsActive());
        promotion.setStartDate(promotionPutVm.getStartDate().toInstant());
        promotion.setEndDate(promotionPutVm.getEndDate().toInstant());
        promotion.setMinimumOrderPurchaseAmount(promotionPutVm.getMinimumOrderPurchaseAmount());

        promotion.setPromotionApplies(PromotionPutVm.createPromotionApplies(promotionPutVm, promotion));

        promotion = promotionRepository.save(promotion);
        return PromotionDetailVm.fromModel(promotion);
    }

    public PromotionListVm getPromotions(
            int pageNo,
            int pageSize,
            String promotionName,
            String couponCode,
            Instant startDate,
            Instant endDate
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Promotion> promotionPage;

        promotionPage = promotionRepository.findPromotions(
                promotionName.trim(),
                couponCode.trim(),
                startDate,
                endDate,
                pageable
        );

        List<PromotionDetailVm> promotionDetailVmList = promotionPage
                .getContent()
                .stream()
                .map(this::toPromotionDetail)
                .toList();

        return PromotionListVm.builder()
                .promotionDetailVmList(promotionDetailVmList)
                .pageNo(promotionPage.getNumber())
                .pageSize(promotionPage.getSize())
                .totalElements(promotionPage.getTotalElements())
                .totalPages(promotionPage.getTotalPages())
                .build();
    }

    private PromotionDetailVm toPromotionDetail(Promotion promotion) {
        List<BrandVm> brandVms = null;
        List<CategoryGetVm> categoryGetVms = null;
        List<ProductVm> productVms = null;
        List<PromotionApply> promotionApplies = promotion.getPromotionApplies();
        switch (promotion.getApplyTo()) {
            case CATEGORY -> categoryGetVms = productService.getCategoryByIds(promotionApplies.stream()
                    .map(PromotionApply::getCategoryId).toList());
            case BRAND -> brandVms = productService.getBrandByIds(promotionApplies.stream()
                    .map(PromotionApply::getBrandId).toList());
            case PRODUCT -> productVms = productService.getProductByIds(promotionApplies.stream()
                    .map(PromotionApply::getProductId).toList());
            default -> {
                break;
            }
        }
        return PromotionDetailVm.fromModel(promotion, brandVms, categoryGetVms, productVms);
    }

    private void validateIfPromotionExistedSlug(String slug) {
        if (promotionRepository.findBySlugAndIsActiveTrue(slug).isPresent()) {
            throw new DuplicatedException(String.format(Constants.ErrorCode.SLUG_ALREADY_EXITED, slug));
        }
    }

    private void validateIfPromotionEndDateIsBeforeStartDate(Instant startDate, Instant endDate) {
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException(String.format(Constants.ErrorCode.DATE_RANGE_INVALID));
        }
    }

    private void validateIfCouponCodeIsExisted(String couponCode) {
        if (promotionRepository.findByCouponCodeAndIsActiveTrue(couponCode).isPresent()) {
            throw new DuplicatedException(Constants.ErrorCode.COUPON_CODE_ALREADY_EXISTED, couponCode);
        }
    }

    public void deletePromotion(Long id) {
        if (promotionUsageRepository.existsByPromotionId(id)) {
            throw new BadRequestException(Constants.ErrorCode.PROMOTION_IN_USE_ERROR_MESSAGE, id);
        }
        promotionRepository.deleteById(id);
    }

    public PromotionDetailVm getPromotion(Long promotionId) {
        Optional<Promotion> promotionOp = promotionRepository.findById(promotionId);
        if (promotionOp.isEmpty()) {
            throw new NotFoundException(Constants.ErrorCode.PROMOTION_NOT_FOUND_ERROR_MESSAGE, promotionId);
        }
        return toPromotionDetail(promotionOp.get());
    }

    public PromotionVerifyResultDto verifyPromotion(PromotionVerifyVm promotionVerifyData) {
        Optional<Promotion> promotionOp =
                promotionRepository.findByCouponCodeAndIsActiveTrue(promotionVerifyData.couponCode());
        if (promotionOp.isEmpty()) {
            throw new NotFoundException(Constants.ErrorCode.PROMOTION_NOT_FOUND_ERROR_MESSAGE,
                    promotionVerifyData.couponCode());
        }

        Promotion promotion = promotionOp.get();

        if (isExhaustedUsageQuantity(promotion)) {
            throw new BadRequestException(Constants.ErrorCode.EXHAUSTED_USAGE_QUANTITY);
        }

        if (isInvalidOrderPrice(promotionVerifyData, promotion)) {
            throw new BadRequestException(Constants.ErrorCode.INVALID_MINIMUM_ORDER_PURCHASE_AMOUNT);
        }

        List<ProductVm> products = getProductsCanApplyPromotion(promotion);
        if (CollectionUtils.isEmpty(products)) {
            throw new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND_TO_APPLY_PROMOTION);
        }

        List<Long> productInPromotionIds = products.stream().map(ProductVm::id).toList();
        List<Long> commonProductIds = new ArrayList<>(promotionVerifyData.productIds());
        commonProductIds.retainAll(productInPromotionIds);
        if (CollectionUtils.isEmpty(commonProductIds)) {
            throw new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND_TO_APPLY_PROMOTION);
        }

        List<ProductVm> productsCanApply = products.stream()
                .filter(product -> commonProductIds.contains(product.id()))
                .sorted(Comparator.comparing(ProductVm::price)).toList();

        return new PromotionVerifyResultDto(true, productsCanApply.getFirst().id(),
                promotion.getCouponCode(),
                promotion.getDiscountType(),
                DiscountType.FIXED.equals(promotion.getDiscountType())
                        ? promotion.getDiscountAmount() : promotion.getDiscountPercentage());
    }

    private boolean isInvalidOrderPrice(PromotionVerifyVm promotionVerifyData, Promotion promotion) {
        return promotionVerifyData.orderPrice() <= 0
                || promotionVerifyData.orderPrice() < promotion.getMinimumOrderPurchaseAmount();
    }

    private boolean isExhaustedUsageQuantity(Promotion promotion) {
        return UsageType.LIMITED.equals(promotion.getUsageType())
                && promotion.getUsageLimit() <= promotion.getUsageCount();
    }

    private List<ProductVm> getProductsCanApplyPromotion(Promotion promotion) {
        switch (promotion.getApplyTo()) {
            case CATEGORY -> {
                List<Long> categoryIds = promotion.getPromotionApplies().stream()
                        .map(PromotionApply::getCategoryId)
                        .toList();
                return productService.getProductByCategoryIds(categoryIds);
            }
            case BRAND -> {
                List<Long> brandIds = promotion.getPromotionApplies().stream()
                        .map(PromotionApply::getBrandId)
                        .toList();
                return productService.getProductByBrandIds(brandIds);
            }
            case PRODUCT -> {
                List<Long> productIds = promotion.getPromotionApplies().stream()
                        .map(PromotionApply::getProductId)
                        .toList();
                return productService.getProductByIds(productIds);
            }
            default -> {
                return Collections.emptyList();
            }
        }
    }

    public void updateUsagePromotion(List<PromotionUsageVm> promotionUsageVms) {
        for (PromotionUsageVm promotionUsageVm : promotionUsageVms) {
            Optional<Promotion> promotion =
                    promotionRepository.findByCouponCodeAndIsActiveTrue(promotionUsageVm.promotionCode());

            if (!promotion.isPresent()) {
                throw new NotFoundException(Constants.ErrorCode.PROMOTION_NOT_FOUND_ERROR_MESSAGE,
                        promotionUsageVm.promotionCode());
            }

            PromotionUsage promotionUsage = PromotionUsage.builder()
                    .promotion(promotion.get())
                    .userId(AuthenticationUtils.extractUserId())
                    .productId(promotionUsageVm.productId())
                    .orderId(promotionUsageVm.orderId())
                    .build();

            promotionUsageRepository.save(promotionUsage);

            Promotion existingPromotion = promotion.get();
            existingPromotion.setUsageCount(existingPromotion.getUsageCount() + 1);
            promotionRepository.save(existingPromotion);
        }
    }

}