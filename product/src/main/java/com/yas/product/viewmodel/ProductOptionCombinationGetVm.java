package com.yas.product.viewmodel;

import com.yas.product.model.ProductOptionCombination;
public record ProductOptionCombinationGetVm(Long id , Long productId , Long productOptionId , String value ) {
    public static ProductOptionCombinationGetVm fromModel(ProductOptionCombination productOptionCombination){
        return new ProductOptionCombinationGetVm(
                productOptionCombination.getId(),
                productOptionCombination.getProduct().getId(),
                productOptionCombination.getProductOption().getId(),
                productOptionCombination.getValue()
        );
    }
}
