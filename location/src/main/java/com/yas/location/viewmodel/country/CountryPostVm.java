package com.yas.location.viewmodel.country;

import jakarta.validation.constraints.NotBlank;

public record CountryPostVm(@NotBlank String name, @NotBlank String code3, Boolean isBillingEnabled,
                            Boolean isShippingEnabled, Boolean isCityEnabled,
                            Boolean isZipCodeEnabled, Boolean isDistrictEnabled) {

}
