package com.yas.product.viewmodel.productoption;

import com.yas.product.model.ProductOption;

public record ProductOptionGetVm(Long id, String name) {
    public static ProductOptionGetVm fromModel(ProductOption productOption){
        return new ProductOptionGetVm(productOption.getId(), productOption.getName());
    }
}
