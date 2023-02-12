package com.yas.product.viewmodel;

import jakarta.validation.constraints.NotEmpty;

public record ProductOptionPostVm(@NotEmpty String name) {
}
