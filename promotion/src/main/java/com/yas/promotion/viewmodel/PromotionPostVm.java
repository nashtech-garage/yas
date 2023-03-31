package com.yas.promotion.viewmodel;

import jakarta.validation.constraints.NotBlank;

import java.time.ZonedDateTime;

public record PromotionPostVm(@NotBlank String name,
                              String description,
                              String couponCode,
                              Long value,
                              Long amount,
                              boolean isActive,
                              ZonedDateTime startDate,
                              ZonedDateTime endDate
) {
}