package com.yas.product.viewmodel.product;

import com.yas.product.validation.ValidateProductPrice;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ProductPutVm(
        @NotEmpty String name,
        String slug,
        @ValidateProductPrice Double price,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean isVisibleIndividually,
        Boolean stockTrackingEnabled,
        Long brandId,
        List<Long> categoryIds,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        Boolean taxIncluded,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        Long thumbnailMediaId,
        List<Long> productImageIds,
        List<ProductVariationPutVm> variations,
        List<ProductOptionValuePutVm> productOptionValues,
        List<Long> relatedProductIds,
        Long taxClassId) {
}

