package com.yas.product.viewmodel;

import javax.validation.constraints.NotEmpty;

public record ProductAttributePostVm(@NotEmpty String name , Long productAttributeGroupId) {
}
