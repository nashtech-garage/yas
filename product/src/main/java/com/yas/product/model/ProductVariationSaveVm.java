package com.yas.product.model;

import com.yas.product.viewmodel.product.ProductProperties;
import java.util.List;
import java.util.Map;

public interface ProductVariationSaveVm extends ProductProperties {
    Double price();

    Long thumbnailMediaId();

    List<Long> productImageIds();

    Map<Long, List<String>> optionValuesByOptionId();
}
