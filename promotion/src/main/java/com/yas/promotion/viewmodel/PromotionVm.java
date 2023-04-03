package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record PromotionVm(Long id,
                        String name,
                        String slug,
                        String couponCode,
                        Long value,
                        Long amount,
                        Boolean isActive,
                        ZonedDateTime startDate,
                        ZonedDateTime endDate
) {
    public static PromotionVm fromModel(Promotion promotion) {
        return PromotionVm.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .slug(promotion.getSlug())
                .value(promotion.getValue())
                .amount(promotion.getAmount())
                .isActive(promotion.getIsActive())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }
}
