package com.yas.customer.viewmodel.UserAddress;

import com.yas.customer.model.UserAddress;
import com.yas.customer.viewmodel.Address.AddressVm;
import lombok.Builder;

@Builder
public record UserAddressVm(
        Long id,
        String userId,
        AddressVm addressGetVm,
        Boolean isActive) {
    public static UserAddressVm fromModel(UserAddress userAddress, AddressVm addressGetVm) {
        return UserAddressVm.builder()
                .id(userAddress.getId())
                .userId(userAddress.getUserId())
                .addressGetVm(addressGetVm)
                .isActive(userAddress.getIsActive())
                .build();
    }
}
