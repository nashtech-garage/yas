package com.yas.product.viewmodel.brand;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yas.product.model.Brand;
import jakarta.validation.constraints.NotBlank;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record BrandPostVm(@NotBlank String name, @NotBlank String slug, boolean isPublish) {

    public Brand toModel() {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug);
        brand.setPublished(isPublish);
        return brand;
    }
}
