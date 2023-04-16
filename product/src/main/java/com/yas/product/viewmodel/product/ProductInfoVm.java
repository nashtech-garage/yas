package com.yas.product.viewmodel.product;

import com.yas.product.model.Product;

public record ProductInfoVm(Long id, String name, String sku) {
    public static ProductInfoVm fromProduct(Product product) {
        return new ProductInfoVm(product.getId(), product.getName(), product.getSku());
    }
}
