package com.yas.promotion.viewmodel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.ZonedDateTime;
import org.hibernate.validator.constraints.Length;

@Builder
public record PromotionPostVm(@NotBlank @Length(max = 450) String name,
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
