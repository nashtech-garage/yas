package com.yas.product.viewmodel.category;

import java.util.List;

public record CategoryListGetVm(
        List<CategoryGetVm> categoryContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}

