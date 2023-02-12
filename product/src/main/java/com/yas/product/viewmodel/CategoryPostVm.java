package com.yas.product.viewmodel;

import jakarta.validation.constraints.NotEmpty;

public record CategoryPostVm (@NotEmpty String name, @NotEmpty String slug, String description, Long parentId, String metaKeywords, String metaDescription, Short displayOrder) {
}
