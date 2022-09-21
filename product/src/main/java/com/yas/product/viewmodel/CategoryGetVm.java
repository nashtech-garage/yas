package com.yas.product.viewmodel;

import com.yas.product.model.Category;

public record CategoryGetVm(long id, String name, String slug, String parentName, long parentId) {
    public static CategoryGetVm fromModel(Category category) {
        Category parent = category.getParent();
        long parentId = parent == null ? -1 : parent.getId();
        String parentName = parent == null ? null : parent.getName();
        return new CategoryGetVm(category.getId(), category.getName(), category.getSlug(), parentName, parentId);
    }
}
