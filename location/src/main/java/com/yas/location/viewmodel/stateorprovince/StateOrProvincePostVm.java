package com.yas.location.viewmodel.stateorprovince;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record StateOrProvincePostVm(@NotBlank @Length(max = 450)  String name,
                                    @Length(max = 255) String code,
                                    @Length(max = 255) String type,
                                    Long countryId) {

}
