package com.yas.inventory.viewmodel.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record WarehousePostVm(String id,
                              @NotBlank @Length(max=450) String name,
                              @Length(max = 450) String contactName,
                              @Length(max = 25) String phone,
                              @Length(max = 450) String addressLine1,
                              @Length(max = 450) String addressLine2,
                              @Length(max = 450) String city,
                              @Length(max = 25) String zipCode,
                              @NotNull Long districtId,
                              @NotNull Long stateOrProvinceId,
                              @NotNull Long countryId) {
}