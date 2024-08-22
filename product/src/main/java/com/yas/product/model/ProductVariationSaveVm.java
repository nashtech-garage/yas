package com.yas.product.model;

import com.yas.product.viewmodel.product.ProductProperties;
import java.util.Map;

public interface ProductVariationSaveVm extends ProductProperties {
    Map<Long, String> optionValuesByOptionId();
}
