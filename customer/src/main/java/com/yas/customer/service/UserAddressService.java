package com.yas.customer.service;

import com.yas.customer.exception.AccessDeniedException;
import com.yas.customer.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.utils.Constants;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.user_address.UserAddressVm;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final LocationService locationService;

    public UserAddressService(UserAddressRepository userAddressRepository, LocationService locationService) {
        this.userAddressRepository = userAddressRepository;
        this.locationService = locationService;
    }

    public List<ActiveAddressVm> getUserAddressList() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userId.equals("anonymousUser"))
            throw new AccessDeniedException("Please login");

        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
        List<AddressVm> addressVmList = locationService.getAddressesByIdList(userAddressList.stream()
                .map(userAddress -> userAddress.getAddressId()).toList());

        List<ActiveAddressVm> addressActiveVms = new ArrayList<>();
        for (UserAddress userAddress : userAddressList) {
            for (AddressVm addressVm : addressVmList) {
                if (userAddress.getAddressId().equals(addressVm.id())) {
                    addressActiveVms.add(new ActiveAddressVm(
                            addressVm.id(),
                            addressVm.contactName(),
                            addressVm.phone(),
                            addressVm.addressLine1(),
                            addressVm.city(),
                            addressVm.zipCode(),
                            addressVm.districtId(),
                            addressVm.stateOrProvinceId(),
                            addressVm.countryId(),
                            userAddress.getIsActive()
                    ));
                    //remove element to reduce the number of iterations
                    addressVmList.remove(addressVm);
                    break;
                }
            }
        }

        //sort by isActive
        Comparator<ActiveAddressVm> comparator = Comparator.comparing(ActiveAddressVm::isActive).reversed();
        return addressActiveVms.stream().sorted( comparator ).collect(Collectors.toList());
    }

    public UserAddressVm createAddress(AddressPostVm addressPostVm) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        AddressVm addressGetVm = locationService.createAddress(addressPostVm);
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
