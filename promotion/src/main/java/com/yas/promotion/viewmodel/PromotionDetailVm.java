package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record PromotionDetailVm(Long id,
                                String name,
                                String slug,
                                String description,
                                String couponCode,
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
                .description(promotion.getDescription())
                .discountPercentage(promotion.getDiscountPercentage())
                .discountAmount(promotion.getDiscountAmount())
                .isActive(promotion.getIsActive())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }
}
