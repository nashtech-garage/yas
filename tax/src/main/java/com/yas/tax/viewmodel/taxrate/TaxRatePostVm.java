package com.yas.tax.viewmodel.taxrate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record TaxRatePostVm(@NotBlank String id,
                            @NotNull Double rate,
                            @Length(max = 25) String zipCode,
                            @NotNull Long taxClassId,
                            @NotNull Long stateOrProvinceId,
                            @NotNull Long countryId) {

}
