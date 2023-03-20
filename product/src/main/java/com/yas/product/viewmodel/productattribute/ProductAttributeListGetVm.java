package com.yas.product.viewmodel.productattribute;

import java.util.List;

public record ProductAttributeListGetVm(
        List<ProductAttributeGetVm> productAttributeContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}

