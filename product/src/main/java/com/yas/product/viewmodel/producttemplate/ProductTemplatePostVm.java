package com.yas.product.viewmodel.producttemplate;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ProductTemplatePostVm(
        @NotBlank String name,
        List<ProductAttributeTemplatePostVm> productAttributeTemplates
) {
}
