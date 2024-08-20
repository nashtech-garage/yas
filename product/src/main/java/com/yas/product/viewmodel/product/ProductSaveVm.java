package com.yas.product.viewmodel.product;

import java.util.List;

public interface ProductSaveVm extends ProductProperties {
    List<? extends ProductProperties> variations();

    @Override
    default Long id() {
        return null;
    }
}
