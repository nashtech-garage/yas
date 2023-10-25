package com.yas.product.viewmodel.producttemplate;


import com.yas.product.model.attribute.ProductAttributeTemplate;
import com.yas.product.viewmodel.productattribute.ProductAttributeVm;

public record ProductAttributeTemplateGetVm(
        ProductAttributeVm productAttribute,
        Integer displayOrder
) {
    public static ProductAttributeTemplateGetVm fromModel(ProductAttributeTemplate productAttributeTemplate){
        return new ProductAttributeTemplateGetVm(
                ProductAttributeVm.fromModel(productAttributeTemplate.getProductAttribute()),
                productAttributeTemplate.getDisplayOrder()
        );
    }
}
