package com.yas.product.viewmodel;

import java.util.List;

public record ProductsGetVm(
        List<ProductThumbnailGetVm> productContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}
