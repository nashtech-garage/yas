package com.yas.rating.viewmodel;

import com.yas.rating.viewmodel.ProductVm;

import java.util.List;

public record ProductListGetVm(
        List<ProductVm> productContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}
