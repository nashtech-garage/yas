package com.yas.product.viewmodel;

import java.util.List;

public record ProductOptionValuePostVm(
        Long ProductId ,
        Long ProductOptionId ,
        String displayType ,
        Integer displayOrder ,
        List<String> value ) {
}
