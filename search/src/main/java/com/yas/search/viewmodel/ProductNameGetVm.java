package com.yas.search.viewmodel;

import com.yas.search.model.Product;

public record ProductNameGetVm(String name) {
    public static ProductNameGetVm fromModel(Product product) {
        return new ProductNameGetVm(
                product.getName()
        );
    }
}
