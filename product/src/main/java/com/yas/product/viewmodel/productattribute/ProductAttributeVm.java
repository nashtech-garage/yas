package com.yas.product.viewmodel.productattribute;

import com.yas.product.model.attribute.ProductAttribute;

public record ProductAttributeVm(Long id, String name) {
    public static ProductAttributeVm fromModel(ProductAttribute productAttribute){
        return new ProductAttributeVm(productAttribute.getId(),productAttribute.getName());
    }
}
