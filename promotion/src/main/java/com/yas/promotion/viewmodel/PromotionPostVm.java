package com.yas.promotion.viewmodel;

import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record PromotionPostVm(@Size(min = 1, max = 450) String name,
                              @NotBlank String slug,
                              String description,
                              String couponCode,
                              int usageLimit,
                              DiscountType discountType,
                              ApplyTo applyTo,
                              UsageType usageType,
                              @Min(0) @Max(100) Long discountPercentage,
                              @Min(0) Long discountAmount,
                              Boolean isActive,
                              ZonedDateTime startDate,
                              ZonedDateTime endDate
) {

}
