package com.yas.product.viewmodel.productoption;

import java.util.List;

public record ProductOptionListGetVm(
        List<ProductOptionGetVm> productOptionContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}

