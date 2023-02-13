package com.yas.product.viewmodel;

import com.yas.product.validation.ValidateProductPrice;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ProductPutVm(
        @NotEmpty String name,
        String slug,
        @ValidateProductPrice Double price,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Long brandId,
        List<Long> categoryIds,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        String metaKeyword,
        String metaDescription,
        Long thumbnailMediaId,
        List<Long> productImageIds
) {
}