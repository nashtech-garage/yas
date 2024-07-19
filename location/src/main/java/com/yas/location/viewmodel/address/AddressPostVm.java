package com.yas.location.viewmodel.address;

import com.yas.location.model.Address;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record AddressPostVm(@Length(max = 450) String contactName,
                            @Length(max = 25) String phone,
                            @Length(max = 450) String addressLine1,
                            @Length(max = 450) String addressLine2,
                            @Length(max = 450) String city,
                            @Length(max = 25) String zipCode,
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
