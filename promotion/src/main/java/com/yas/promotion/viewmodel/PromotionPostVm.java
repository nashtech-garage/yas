package com.yas.promotion.viewmodel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record PromotionPostVm(@Size(min = 1, max = 450) String name,
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
