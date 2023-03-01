package com.yas.product.viewmodel.product;

import com.yas.product.model.ProductOptionValue;

public record ProductVariationVm(Long id, Long productOptionId, String productOptionValue, String productOptionName) {
    public static ProductVariationVm fromModel(ProductOptionValue productOptionValue){
        return new ProductVariationVm(productOptionValue.getId(), productOptionValue.getProductOption().getId(), productOptionValue.getValue(), productOptionValue.getProductOption().getName());
    }
}
