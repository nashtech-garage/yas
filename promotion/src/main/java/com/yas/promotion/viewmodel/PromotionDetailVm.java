package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
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
                                ZonedDateTime startDate,
                                ZonedDateTime endDate
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
}
