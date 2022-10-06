package com.yas.product.viewmodel;

public record ProductOptionValuePostVm(
        Long ProductId ,
        Long ProductOptionId ,
        String displayType ,
        Integer displayOrder ,
        String value ) {
}
