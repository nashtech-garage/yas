package com.yas.product.viewmodel;

import com.yas.product.model.Brand;
import javax.validation.constraints.NotEmpty;

public record BrandPostVm(@NotEmpty String name, @NotEmpty String slug) {

    public Brand toModel(){
        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(name);
        return brand;
    }
}
