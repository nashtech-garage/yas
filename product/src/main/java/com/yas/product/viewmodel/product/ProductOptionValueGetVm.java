package com.yas.product.viewmodel.product;

import com.yas.product.model.ProductOptionValue;

public record ProductOptionValueGetVm(
        Long id,
        Long productOptionId,
        String productOptionValue,
        String productOptionName
) {
    public static ProductOptionValueGetVm fromModel(ProductOptionValue productOptionValue) {
        return new ProductOptionValueGetVm(
                productOptionValue.getId(),
                productOptionValue.getProductOption().getId(),
                productOptionValue.getValue(),
                productOptionValue.getProductOption().getName()
        );
    }
}
