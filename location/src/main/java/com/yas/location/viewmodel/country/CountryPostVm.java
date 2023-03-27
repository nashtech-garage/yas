package com.yas.location.viewmodel.country;

import com.yas.location.model.Country;
import jakarta.validation.constraints.NotBlank;
public record CountryPostVm(@NotBlank String name, @NotBlank String code3, Boolean isBillingEnabled,
                            Boolean isShippingEnabled, Boolean isCityEnabled, Boolean isZipCodeEnabled, Boolean isDistrictEnabled) {

    public Country toModel(){
        Country country = new Country();
        country.setName(name);
        country.setCode3(code3);
        country.setIsBillingEnabled(isBillingEnabled);
        country.setIsShippingEnabled(isShippingEnabled);
        country.setIsCityEnabled(isCityEnabled);
        country.setIsZipCodeEnabled(isZipCodeEnabled);
        country.setIsDistrictEnabled(isDistrictEnabled);

        return country;
    }
}

