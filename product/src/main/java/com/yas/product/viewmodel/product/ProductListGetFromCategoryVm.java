package com.yas.product.viewmodel.product;

import java.util.List;

public record ProductListGetFromCategoryVm(
        List<ProductThumbnailVm> productContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}
