package com.yas.product.viewmodel;

import com.yas.product.model.Product;
public record ProductVariationGetVm(Long id , String NameProduct , String Sku , String Gtin , Double Price ) {
    public static ProductVariationGetVm fromModel(Product product){
        return new ProductVariationGetVm(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getGtin(),
                product.getPrice()
        );
    }
}
