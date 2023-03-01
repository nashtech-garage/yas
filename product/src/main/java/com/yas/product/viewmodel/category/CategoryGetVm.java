package com.yas.product.viewmodel.category;

import com.yas.product.model.Category;

public record CategoryGetVm(long id, String name, String slug, long parentId) {
    public static CategoryGetVm fromModel(Category category) {
        Category parent = category.getParent();
        long parentId = parent == null ? -1 : parent.getId();
        return new CategoryGetVm(category.getId(), category.getName(), category.getSlug(), parentId);
    }
}
