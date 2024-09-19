package com.yas.product.viewmodel.product;

import java.util.List;
import java.util.Map;

public record ProductVariationPutVm(
        Long id,
        String name,
        String slug,
        String sku,
        String gtin,
        Double price,
        Long thumbnailMediaId,
        List<Long> productImageIds,
        Map<Long, String> optionValuesByOptionId
) implements ProductVariationSaveVm {
}
