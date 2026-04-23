package com.yas.promotion.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import com.yas.promotion.viewmodel.PromotionDto;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class PromotionValidatorTest {

    private final PromotionValidator validator = new PromotionValidator();

    @Test
    void testIsValid_whenAppleToProduct_isValidTrue() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.LIMITED);
        dto.setUsageLimit(10);
        dto.setDiscountType(DiscountType.FIXED);
        dto.setDiscountAmount(5L);
        dto.setApplyTo(ApplyTo.PRODUCT);
        dto.setProductIds(Collections.singletonList(1L));

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertTrue(isValid);
    }

    @Test
    void testIsValid_whenAppleToCategory_isValidTrue() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.LIMITED);
        dto.setUsageLimit(10);
        dto.setDiscountType(DiscountType.FIXED);
        dto.setDiscountAmount(5L);
        dto.setApplyTo(ApplyTo.CATEGORY);
        dto.setCategoryIds(Collections.singletonList(1L));

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertTrue(isValid);
    }

    @Test
    void testIsValid_whenAppleToBrand_isValidTrue() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.LIMITED);
        dto.setUsageLimit(10);
        dto.setDiscountType(DiscountType.FIXED);
        dto.setDiscountAmount(5L);
        dto.setApplyTo(ApplyTo.BRAND);
        dto.setBrandIds(Collections.singletonList(1L));

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertTrue(isValid);
    }

    @Test
    void testIsValid_whenUsageLimitIs0_isValidFalse() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.LIMITED);
        dto.setUsageLimit(0);
        dto.setDiscountType(DiscountType.PERCENTAGE);
        dto.setDiscountPercentage(1L);
        dto.setDiscountAmount(5L);
        dto.setApplyTo(ApplyTo.CATEGORY);
        dto.setCategoryIds(Collections.singletonList(1L));

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertFalse(isValid);
    }

    @Test
    void testIsValid_whenDiscountTypeIsFixedAndDiscountAmountIs0_isValidFalse() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.UNLIMITED);
        dto.setUsageLimit(10);
        dto.setDiscountType(DiscountType.FIXED);
        dto.setDiscountAmount(0L);
        dto.setApplyTo(ApplyTo.BRAND);
        dto.setBrandIds(Collections.singletonList(1L));

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertFalse(isValid);
    }

    @Test
    void testIsValid_whenApplyToIsProductAndProductIdsIdEmpty_isValidFalse() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.UNLIMITED);
        dto.setUsageLimit(10);
        dto.setDiscountType(DiscountType.PERCENTAGE);
        dto.setDiscountPercentage(10L);
        dto.setApplyTo(ApplyTo.PRODUCT);
        dto.setProductIds(Collections.emptyList());

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertFalse(isValid);
    }

    @Test
    void testIsValid_whenApplyToIsBrandAndBrandIdsIdEmpty_isValidFalse() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.UNLIMITED);
        dto.setUsageLimit(9);
        dto.setDiscountType(DiscountType.PERCENTAGE);
        dto.setDiscountPercentage(10L);
        dto.setApplyTo(ApplyTo.BRAND);
        dto.setBrandIds(Collections.emptyList());

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertFalse(isValid);
    }

    @Test
    void testIsValid_whenApplyToIsCategoryAndCategoryIdsIdEmpty_isValidFalse() {

        PromotionDto dto = new PromotionDto();
        dto.setUsageType(UsageType.UNLIMITED);
        dto.setUsageLimit(11);
        dto.setDiscountType(DiscountType.PERCENTAGE);
        dto.setDiscountPercentage(10L);
        dto.setApplyTo(ApplyTo.CATEGORY);
        dto.setCategoryIds(Collections.emptyList());

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean isValid = validator.isValid(dto, context);

        assertFalse(isValid);
    }
}
