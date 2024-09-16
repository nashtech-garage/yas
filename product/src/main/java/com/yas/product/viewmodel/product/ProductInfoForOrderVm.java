package com.yas.product.viewmodel.product;

import com.yas.product.model.Product;

public record ProductInfoForOrderVm(Long parentId, Long id, String name, Double price, Long taxClassId) {
    public static ProductInfoForOrderVm fromProduct(Product product) {
        Long parentId = product.getParent() == null ? null : product.getParent().getId();
        return new ProductInfoForOrderVm(parentId,
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getTaxClassId());
    }
}
