package com.yas.product.viewmodel.brand;

import com.yas.product.model.Brand;
import jakarta.validation.constraints.NotEmpty;

public record BrandPostVm(@NotEmpty String name, @NotEmpty String slug) {

    public Brand toModel(){
        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug);
        return brand;
    }
}
