package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record PromotionDetailVm(Long id,
                                String name,
                                String description,
                                String couponCode,
                                Long value,
                                Long amount,
                                boolean isActive,
                                ZonedDateTime startDate,
                                ZonedDateTime endDate
                        ) {
    public static PromotionDetailVm fromModel(Promotion promotion){
        return PromotionDetailVm.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .value(promotion.getValue())
                .amount(promotion.getAmount())
                .isActive(promotion.isActive())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }
}
