package com.yas.product.viewmodel;

import javax.validation.constraints.NotBlank;

public record CategoryPostVm (@NotBlank String name, @NotBlank String slug, String description, Long parentId) {
}
