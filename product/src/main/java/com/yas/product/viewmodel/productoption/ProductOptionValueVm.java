package com.yas.product.viewmodel.productoption;

import java.util.List;

public record ProductOptionValueVm(
        Long ProductOptionId,
        String displayType,
        Integer displayOrder,
        List<String> value) {
}
