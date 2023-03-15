package com.yas.product.viewmodel.brand;

import com.yas.product.model.Brand;
import jakarta.validation.constraints.NotBlank;

public record BrandPostVm(@NotBlank String name, @NotBlank String slug) {

    public Brand toModel(){
        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug);
        return brand;
    }
}
