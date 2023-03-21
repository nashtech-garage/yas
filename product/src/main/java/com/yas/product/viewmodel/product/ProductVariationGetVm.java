package com.yas.product.viewmodel.product;

import java.util.List;

public record ProductVariationGetVm(
        Long id,
        String name,
        String slug,
        String sku,
        String gtin,
        Double price,
        String thumbnailMediaUrl,
        List<String> productImageMediaUrls
) {
}
