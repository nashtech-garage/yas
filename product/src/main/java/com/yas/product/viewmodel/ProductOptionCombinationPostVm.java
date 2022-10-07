package com.yas.product.viewmodel;

public record ProductOptionCombinationPostVm(
        String value,
        Short displayOrder,
        Long productId,
        Long productOptionId
) {}
