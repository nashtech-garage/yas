package com.yas.product.viewmodel.producttemplate;

import java.util.List;

public record ProductTemplateVm(
        Long id,
        String name,
        List<ProductAttributeTemplateGetVm> productAttributeTemplates) {
}
