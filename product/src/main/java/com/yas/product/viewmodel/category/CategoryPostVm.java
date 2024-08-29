package com.yas.product.viewmodel.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CategoryPostVm(@NotBlank String name, @NotBlank String slug, String description, Long parentId,
                             String metaKeywords, String metaDescription, Short displayOrder, Boolean isPublish,
                             Long imageId) {

}
