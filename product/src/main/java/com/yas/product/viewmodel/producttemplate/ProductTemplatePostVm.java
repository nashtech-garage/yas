package com.yas.product.viewmodel.producttemplate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record ProductTemplatePostVm(
        @NotBlank String name,
        List<ProductAttributeTemplatePostVm> productAttributeTemplates
) {
}
