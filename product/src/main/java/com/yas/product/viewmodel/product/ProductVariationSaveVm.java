package com.yas.product.viewmodel.product;

import java.util.List;
import java.util.Map;

public interface ProductVariationSaveVm extends ProductProperties {
    Double price();

    Long thumbnailMediaId();

    List<Long> productImageIds();

    Map<Long, String> optionValuesByOptionId();
}
