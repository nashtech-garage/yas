package com.yas.product.viewmodel.product;

import com.yas.product.model.ProductVariationSaveVm;
import java.util.List;

public interface ProductSaveVm<T extends ProductVariationSaveVm> extends ProductProperties {
    List<T> variations();

    Boolean isPublished();

    @Override
    default Long id() {
        return null;
    }
}
