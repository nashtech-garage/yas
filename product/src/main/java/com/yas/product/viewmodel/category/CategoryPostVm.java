package com.yas.product.viewmodel.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryPostVm (@NotBlank String name, @NotBlank String slug, String description, Long parentId, String metaKeywords, String metaDescription, Short displayOrder) {
}
