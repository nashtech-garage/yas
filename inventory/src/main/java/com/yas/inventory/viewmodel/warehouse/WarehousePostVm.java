package com.yas.inventory.viewmodel.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record WarehousePostVm(String id,
                              @Size(min = 1, max = 450) String name,
                              @Size(max = 450) String contactName,
                              @Size(max = 25) String phone,
                              @Size(max = 450) String addressLine1,
                              @Size(max = 450) String addressLine2,
                              @Size(max = 450) String city,
                              @Size(max = 25) String zipCode,
                              @NotNull Long districtId,
                              @NotNull Long stateOrProvinceId,
                              @NotNull Long countryId) {

}