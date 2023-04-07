package com.yas.customer.service;

import com.yas.customer.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.utils.Constants;
import com.yas.customer.viewmodel.Address.AddressActiveVm;
import com.yas.customer.viewmodel.Address.AddressGetVm;
import com.yas.customer.viewmodel.Address.AddressPostVm;
import com.yas.customer.viewmodel.UserAddress.UserAddressVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final LocationService locationService;

    public UserAddressService(UserAddressRepository userAddressRepository, LocationService locationService) {
        this.userAddressRepository = userAddressRepository;
        this.locationService = locationService;
    }

    public List<AddressActiveVm> getUserAddressList() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);

        List<AddressGetVm> addressGetVms = locationService.getAddressesByIdList(userAddressList.stream()
                .map(userAddress -> userAddress.getAddressId()).toList());

        return IntStream.range(0, userAddressList.size())
                .mapToObj(i -> new AddressActiveVm(addressGetVms.get(i).id(),
                        addressGetVms.get(i).contactName(), addressGetVms.get(i).phone(),
                        addressGetVms.get(i).addressLine1(), addressGetVms.get(i).city(),
                        addressGetVms.get(i).zipCode(), addressGetVms.get(i).districtId(),
                        addressGetVms.get(i).stateOrProvinceId(), addressGetVms.get(i).countryId(),
                        userAddressList.get(i).getIsActive()))
                .collect(Collectors.toList());

    }

    public UserAddressVm createAddress(AddressPostVm addressPostVm) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        AddressGetVm addressGetVm = locationService.createAddress(addressPostVm);

        UserAddress userAddress = UserAddress.builder()
                .userId(userId)
                .addressId(addressGetVm.id())
                .isActive(false)
                .build();

        return UserAddressVm.fromModel(userAddressRepository.save(userAddress), addressGetVm);

    }

    public void deleteAddress(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAddress userAddress = userAddressRepository.findOneByUserIdAndAddressId(userId, id);
        if (userAddress == null) {
            throw new NotFoundException(Constants.ERROR_CODE.USER_ADDRESS_NOT_FOUND);
        }
        userAddressRepository.delete(userAddress);
    }

    public void chooseDefaultAddress(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
        List<UserAddress> newUserAddressList = new ArrayList<>();
        for (UserAddress userAddress : userAddressList) {
            if (userAddress.getAddressId() == id) {
                userAddress.setIsActive(true);
            } else {
                userAddress.setIsActive(false);
            }
            newUserAddressList.add(userAddress);
        }
        userAddressRepository.saveAll(newUserAddressList);
    }
}
