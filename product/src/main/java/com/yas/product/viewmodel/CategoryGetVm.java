package com.yas.product.viewmodel;

import com.yas.product.model.Category;

public record CategoryGetVm (long id, String name, String slug) {
    public static CategoryGetVm fromModel(Category category){
        return new CategoryGetVm(category.getId(), category.getName(), category.getSlug());
    }
}
