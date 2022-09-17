package com.yas.product.viewmodel;

import com.yas.product.model.Product;

//Will be resolved later, if I change ProductGetDetailVm, it will be effected to other code, unit test...
public record ProductGetDetailVm(long id, String name, String slug, String shortDescription, String description, String specification, String sku, String gtin, String metaKeyword, String metaDescription, String thumbnailUrl) {

    public static ProductGetDetailVm fromModel(Product product, String thumbnailUrl){
        return new ProductGetDetailVm(product.getId(), product.getName(), product.getSlug(), product.getShortDescription(), product.getDescription(), product.getSpecification(), product.getSku(), product.getGtin(), product.getMetaKeyword(), product.getMetaDescription(), thumbnailUrl);
    }
}