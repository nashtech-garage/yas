package com.yas.product.viewmodel.product;

import jakarta.validation.constraints.Min;

import java.util.List;

public record ProductVariationPostVm(
        String name,
        String slug,
        String sku,
        String gtin,
        Double price,
        Long thumbnailMediaId,
        @Min(1) Integer remainingQuantity,
        List<Long> productImageIds
) {
}
