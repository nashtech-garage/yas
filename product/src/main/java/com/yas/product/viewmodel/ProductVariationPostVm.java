package com.yas.product.viewmodel;

public record ProductVariationPostVm(
        String NameOptionCombinations,
        Long productId,
        String Sku,
        String Gtin,
        Double price
){}
