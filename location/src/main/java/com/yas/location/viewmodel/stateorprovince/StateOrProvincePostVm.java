package com.yas.location.viewmodel.stateorprovince;

import jakarta.validation.constraints.NotBlank;

public record StateOrProvincePostVm(@NotBlank String name, String code, String type,
                                    Long countryId) {

}
