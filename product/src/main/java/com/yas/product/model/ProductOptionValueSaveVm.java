package com.yas.product.model;
import java.util.List;
public record ProductOptionValueSaveVm(
        Long productOptionId,
        String displayType,
        Integer displayOrder,
        List<String> value) {

}
