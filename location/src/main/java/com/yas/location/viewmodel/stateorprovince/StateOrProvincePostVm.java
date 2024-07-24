package com.yas.location.viewmodel.stateorprovince;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record StateOrProvincePostVm(@Size(min = 1, max = 450) String name,
                                    @Size(max = 255) String code,
                                    @Size(max = 255) String type,
                                    Long countryId) {

}
