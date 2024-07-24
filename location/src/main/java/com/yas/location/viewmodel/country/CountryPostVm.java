package com.yas.location.viewmodel.country;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CountryPostVm(@NotBlank String id,
                            @Size(min = 1, max = 3) String code2,
                            @Size(min = 1, max = 450) String name,
                            @Size(max = 3) String code3,
                            Boolean isBillingEnabled,
                            Boolean isShippingEnabled,
                            Boolean isCityEnabled,
                            Boolean isZipCodeEnabled,
                            Boolean isDistrictEnabled) {

}
