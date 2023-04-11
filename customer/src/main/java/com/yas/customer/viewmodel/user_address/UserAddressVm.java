package com.yas.customer.viewmodel.user_address;

import com.yas.customer.model.UserAddress;
import com.yas.customer.viewmodel.address.AddressVm;
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
