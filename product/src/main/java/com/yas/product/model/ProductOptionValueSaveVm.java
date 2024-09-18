package com.yas.product.model;

//public interface ProductOptionValueSaveVm {
import java.util.List;

//    Long productOptionId();
//}
public record ProductOptionValueSaveVm(
        Long productOptionId,
        String displayType,
        Integer displayOrder,
        List<String> value) {

}
