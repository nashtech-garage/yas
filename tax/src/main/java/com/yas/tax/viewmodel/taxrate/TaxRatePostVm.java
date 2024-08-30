package com.yas.tax.viewmodel.taxrate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaxRatePostVm(@NotNull Double rate,
                            @Size(max = 25) String zipCode,
                            @NotNull Long taxClassId,
                            @NotNull Long stateOrProvinceId,
                            @NotNull Long countryId) {

}
