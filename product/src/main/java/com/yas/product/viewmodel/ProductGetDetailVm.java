package com.yas.product.viewmodel;

import com.yas.product.model.Product;

public record ProductGetDetailVm(long id, String name, String slug) {
    public static ProductGetDetailVm fromModel(Product product){
        return new ProductGetDetailVm(product.getId(), product.getName(), product.getSlug());
    }
}
