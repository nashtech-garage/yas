package com.yas.product.viewmodel.product;

import com.yas.product.model.ProductOptionCombination;

public record ProductOptionCombinationGetVm(
        Long id,
        Long productOptionId,
        String productOptionValue,
        String productOptionName
) {
    public static ProductOptionCombinationGetVm fromModel(ProductOptionCombination productOptionCombination) {
        return new ProductOptionCombinationGetVm(
                productOptionCombination.getId(),
                productOptionCombination.getProductOption().getId(),
                productOptionCombination.getValue(),
                productOptionCombination.getProductOption().getName()
        );
    }
}
