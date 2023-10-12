package com.yas.product.viewmodel.producttemplate;

import com.yas.product.viewmodel.productattribute.ProductAttributeVm;

import java.util.List;

public record ProductTemplateVm(Long id, String name, List<ProductAttributeVm> productAttributeVms) {
}
