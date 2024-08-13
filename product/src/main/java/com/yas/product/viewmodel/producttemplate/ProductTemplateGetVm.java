package com.yas.product.viewmodel.producttemplate;

import com.yas.product.model.attribute.ProductTemplate;

public record ProductTemplateGetVm(Long id, String name) {

    public static ProductTemplateGetVm fromModel(ProductTemplate productTemplate) {
        return new ProductTemplateGetVm(
                productTemplate.getId(),
                productTemplate.getName()
        );
    }
}
