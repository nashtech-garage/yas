package com.yas.product.viewmodel.producttemplate;


import java.util.List;

public record ProductTemplateListGetVm(
        List<ProductTemplateGetVm> productTemplateVms,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {}
