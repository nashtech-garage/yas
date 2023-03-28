package com.yas.location.viewmodel.stateorprovince;

import jakarta.validation.constraints.NotBlank;

public record StateOrProvincePostVm(@NotBlank String name, @NotBlank String code, String type,
                                    Long countryId) {

}
