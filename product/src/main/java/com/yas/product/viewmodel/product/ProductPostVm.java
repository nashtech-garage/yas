package com.yas.product.viewmodel.product;

import com.yas.product.validation.ValidateProductPrice;
<<<<<<< Updated upstream
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
=======
import jakarta.validation.constraints.Min;
>>>>>>> Stashed changes
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
        @Min(1) Integer remainingQuantity,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean isVisibleIndividually,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        Long thumbnailMediaId,
        List<Long> productImageIds,
        List<ProductVariationPostVm> variations,
        List<ProductOptionValuePostVm> productOptionValues) {
}
