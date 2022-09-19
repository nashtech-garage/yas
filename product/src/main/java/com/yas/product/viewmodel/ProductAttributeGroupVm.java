package com.yas.product.viewmodel;

import com.yas.product.model.attribute.ProductAttributeGroup;

public record ProductAttributeGroupVm(Long id, String name) {
    public static ProductAttributeGroupVm fromModel(ProductAttributeGroup productAttributeGroup) {
        return new ProductAttributeGroupVm(productAttributeGroup.getId(), productAttributeGroup.getName());
    }
}
