package com.yas.product.viewmodel;

import com.yas.product.model.attribute.ProductAttribute;

public record ProductAttributeGetVm(long id , String name , String productAttributeGroup) {

    public static ProductAttributeGetVm fromModel(ProductAttribute productAttribute){
        return new ProductAttributeGetVm(productAttribute.getId(), productAttribute.getName() , productAttribute.getProductAttributeGroup().getName());
    }
}
