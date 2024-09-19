package com.yas.product.viewmodel.productoption;

import java.util.List;

public record ProductOptionValueSaveVm(
        Long productOptionId,
        String displayType,
        Integer displayOrder,
        List<String> value) {

}
