package com.yas.location.viewmodel.address;

import com.yas.location.model.Address;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddressPostVm(@Size(max = 450) String contactName,
                            @Size(max = 25) String phone,
                            @Size(max = 450) String addressLine1,
                            @Size(max = 450) String addressLine2,
                            @Size(max = 450) String city,
                            @Size(max = 25) String zipCode,
                            @NotNull Long districtId,
                            @NotNull Long stateOrProvinceId,
                            @NotNull Long countryId) {

    public static Address fromModel(AddressPostVm dto) {
        return Address.builder()
            .contactName(dto.contactName)
            .phone(dto.phone)
            .addressLine1(dto.addressLine1)
            .addressLine2(dto.addressLine2)
            .city(dto.city())
            .zipCode(dto.zipCode)
            .build();
    }
}
