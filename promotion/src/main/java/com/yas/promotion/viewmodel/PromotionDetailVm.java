package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record PromotionDetailVm(Long id,
                                String name,
                                String slug,
                                String description,
                                String couponCode,
                                int usageLimit,
                                int usageCount,
                                DiscountType discountType,
                                ApplyTo applyTo,
                                UsageType usageType,
                                Long discountPercentage,
                                Long discountAmount,
                                Boolean isActive,
                                Instant startDate,
                                Instant endDate,
                                List<BrandVm> brands,
                                List<CategoryGetVm> categories,
                                List<ProductVm> products
) {
    public static PromotionDetailVm fromModel(Promotion promotion) {
        return PromotionDetailVm.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .slug(promotion.getSlug())
                .couponCode(promotion.getCouponCode())
                .usageLimit(promotion.getUsageLimit())
                .usageCount(promotion.getUsageCount())
                .discountType(promotion.getDiscountType())
                .applyTo(promotion.getApplyTo())
                .usageType(promotion.getUsageType())
                .description(promotion.getDescription())
                .discountPercentage(promotion.getDiscountPercentage())
                .discountAmount(promotion.getDiscountAmount())
                .isActive(promotion.getIsActive())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }

    public static PromotionDetailVm fromModel(
        Promotion promotion, List<BrandVm> brands, List<CategoryGetVm> categories, List<ProductVm> products) {
        return PromotionDetailVm.builder()
            .id(promotion.getId())
            .name(promotion.getName())
            .slug(promotion.getSlug())
            .couponCode(promotion.getCouponCode())
            .usageLimit(promotion.getUsageLimit())
            .usageCount(promotion.getUsageCount())
            .discountType(promotion.getDiscountType())
            .applyTo(promotion.getApplyTo())
            .usageType(promotion.getUsageType())
            .description(promotion.getDescription())
            .discountPercentage(promotion.getDiscountPercentage())
            .discountAmount(promotion.getDiscountAmount())
            .isActive(promotion.getIsActive())
            .startDate(promotion.getStartDate())
            .endDate(promotion.getEndDate())
            .brands(brands)
            .categories(categories)
            .products(products)
            .build();
    }
}
