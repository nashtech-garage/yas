package com.yas.tax.viewmodel.taxrate;

import jakarta.validation.constraints.NotBlank;

public record TaxRatePostVm(@NotBlank String id, Double rate, String zipCode,
                            Long taxClassId, Long stateOrProvinceId, Long countryId) {

}
