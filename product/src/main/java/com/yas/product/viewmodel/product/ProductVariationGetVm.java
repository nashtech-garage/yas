package com.yas.product.viewmodel.product;

import java.util.List;
import java.util.Map;

public record ProductVariationGetVm(
        Long id,
        String name,
        String slug,
        String sku,
        String gtin,
        Double price,
        String thumbnailMediaUrl,
        List<String> productImageMediaUrls,
        Map<String, String> options
) {
}
