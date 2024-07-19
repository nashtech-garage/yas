package com.yas.location.viewmodel.country;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CountryPostVm(@NotBlank String id,
                            @NotBlank @Length(max = 3) String code2,
                            @NotBlank @Length(max = 450) String name,
                            @Length(max = 3) String code3,
                            Boolean isBillingEnabled,
                            Boolean isShippingEnabled,
                            Boolean isCityEnabled,
                            Boolean isZipCodeEnabled,
                            Boolean isDistrictEnabled) {

}
