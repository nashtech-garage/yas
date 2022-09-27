package com.yas.product.viewmodel;

import com.yas.product.model.Category;

public record CategoryGetDetailVm(long Id, String name, String slug, String description, long parentId, String metaKeywords, String metaDescription, short displayOrder) {
    public static CategoryGetDetailVm fromModel(Category category){
        if(category.getParent() != null)
            return new CategoryGetDetailVm(category.getId(), category.getName(), category.getSlug(), category.getDescription(), category.getParent().getId(),  category.getMetaKeyword(), category.getMetaDescription(), category.getDisplayOrder());
        else
            return new CategoryGetDetailVm(category.getId(), category.getName(), category.getSlug(), category.getDescription(), 0L,  category.getMetaKeyword(), category.getMetaDescription(), category.getDisplayOrder());
    }
}
