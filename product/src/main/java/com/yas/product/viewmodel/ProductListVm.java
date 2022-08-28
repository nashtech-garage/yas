package com.yas.product.viewmodel;

import com.yas.product.model.Product;

public record ProductListVm(Long id, String name, String slug) {
    public static ProductListVm fromModel(Product product) {
        return new ProductListVm(product.getId(), product.getName(), product.getSlug());
    }
}
