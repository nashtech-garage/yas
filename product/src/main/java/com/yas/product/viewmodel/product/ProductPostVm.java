package com.yas.product.viewmodel.product;

import com.yas.product.validation.ValidateProductPrice;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
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
        Boolean stockTrackingEnabled,

        Boolean taxIncluded,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        Long thumbnailMediaId,
        List<Long> productImageIds,
        List<ProductVariationPostVm> variations,
        List<ProductOptionValuePostVm> productOptionValues,
        List<Long> relatedProductIds) {
}
