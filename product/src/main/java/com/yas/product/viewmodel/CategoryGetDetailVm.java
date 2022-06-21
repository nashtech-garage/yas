package com.yas.product.viewmodel;

import com.yas.product.model.Category;

public record CategoryGetDetailVm(long Id, String name, String slug, String description) {
    public static CategoryGetDetailVm fromModel(Category category){
        return  new CategoryGetDetailVm(category.getId(), category.getName(), category.getSlug(), category.getDescription());
    }
}
