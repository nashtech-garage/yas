package com.yas.product.viewmodel.productattribute;

import java.util.List;

public record ProductAttributeGroupListGetVm(
        List<ProductAttributeGroupGetVm> productAttributeGroupContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}

