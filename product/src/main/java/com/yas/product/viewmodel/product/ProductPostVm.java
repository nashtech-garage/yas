package com.yas.product.viewmodel.product;

import com.yas.product.validation.ValidateProductPrice;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ProductPostVm(
        @NotBlank String name,
        @NotBlank String slug,
        Long brandId,
        List<Long> categoryIds,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        @ValidateProductPrice Double price,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean isVisibleIndividually,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        Long parentId) {
}
