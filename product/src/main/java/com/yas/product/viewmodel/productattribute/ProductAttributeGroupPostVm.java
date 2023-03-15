package com.yas.product.viewmodel.productattribute;

import com.yas.product.model.attribute.ProductAttributeGroup;

import jakarta.validation.constraints.NotBlank;

public record ProductAttributeGroupPostVm(@NotBlank String name) {

    public ProductAttributeGroup toModel(){
        ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setName(name);
        return productAttributeGroup;
    }
}
