package com.yas.product.viewmodel.product;

import java.util.List;

public interface ProductSaveVm extends HasProductProperties {
    List<? extends HasProductProperties> variations();

    @Override
    default Long id() {
        return null;
    }
}
