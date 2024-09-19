package com.yas.product.viewmodel.product;

import java.util.List;
import java.util.Map;

public record ProductVariationPostVm(
        String name,
        String slug,
        String sku,
        String gtin,
        Double price,
        Long thumbnailMediaId,
        List<Long> productImageIds,
        Map<Long, String> optionValuesByOptionId
) implements ProductVariationSaveVm {

    @Override
    public Long id() {
        return null;
    }
}
