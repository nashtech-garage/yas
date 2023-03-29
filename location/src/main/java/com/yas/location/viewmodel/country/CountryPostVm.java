package com.yas.location.viewmodel.country;

import jakarta.validation.constraints.NotBlank;

public record CountryPostVm(@NotBlank String id, @NotBlank String name, String code3, Boolean isBillingEnabled,
                            Boolean isShippingEnabled, Boolean isCityEnabled,
                            Boolean isZipCodeEnabled, Boolean isDistrictEnabled) {

}
