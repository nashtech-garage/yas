package com.yas.product.viewmodel;

import javax.validation.constraints.NotEmpty;

public record ProductOptionPostVm(@NotEmpty String name) {
}
