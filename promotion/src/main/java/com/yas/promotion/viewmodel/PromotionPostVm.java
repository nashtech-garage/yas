package com.yas.promotion.viewmodel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record PromotionPostVm(@NotBlank String name,
                              @NotBlank String slug,
                              String description,
                              String couponCode,
                              Long discountPercentage,
                              Long discountAmount,
                              Boolean isActive,
                              ZonedDateTime startDate,
                              ZonedDateTime endDate
) {
}
