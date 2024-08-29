package com.yas.product.viewmodel.product;

import com.yas.product.model.ProductVariationSaveVm;
import java.util.List;

public interface ProductSaveVm extends ProductProperties {
    List<? extends ProductVariationSaveVm> variations();

    Boolean isPublished();

    @Override
    default Long id() {
        return null;
    }
}
