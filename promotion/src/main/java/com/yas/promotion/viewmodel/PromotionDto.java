package com.yas.promotion.viewmodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize
public class PromotionDto {
    @NotNull
    private ApplyTo applyTo;
    @NotNull
    private UsageType usageType;
    @NotNull
    private DiscountType discountType;
    @Max(100)
    private Long discountPercentage;
    private Long discountAmount;
    private int usageLimit;
    private List<Long> productIds;
    private List<Long> brandIds;
    private List<Long> categoryIds;

}
