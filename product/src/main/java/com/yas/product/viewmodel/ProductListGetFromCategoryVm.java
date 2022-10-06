package com.yas.product.viewmodel;

import java.util.List;

public record ProductListGetFromCategoryVm(
        List<ProductThumbnailVm> productContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {}
