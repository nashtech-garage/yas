package com.yas.product.viewmodel.productoption;

import java.util.List;

public record ProductOptionValuePostVm(
        Long ProductId ,
        Long ProductOptionId ,
        String displayType ,
        Integer displayOrder ,
        List<String> value ) {
}
