package com.yas.customer.service;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.utils.Constants;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (userId.equals("anonymousUser")) {
            throw new AccessDeniedException(Constants.ErrorCode.UNAUTHENTICATED);
        }

        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
        List<AddressDetailVm> addressVmList = locationService.getAddressesByIdList(
            userAddressList.stream()
                .map(UserAddress::getAddressId)
                .collect(Collectors.toList()));

        List<ActiveAddressVm> addressActiveVms = userAddressList.stream()
            .flatMap(userAddress -> addressVmList.stream()
                .filter(addressDetailVm -> userAddress.getAddressId().equals(addressDetailVm.id()))
                .map(addressDetailVm -> new ActiveAddressVm(
                    addressDetailVm.id(),
                    addressDetailVm.contactName(),
                    addressDetailVm.phone(),
                    addressDetailVm.addressLine1(),
                    addressDetailVm.city(),
                    addressDetailVm.zipCode(),
                    addressDetailVm.districtId(),
                    addressDetailVm.districtName(),
                    addressDetailVm.stateOrProvinceId(),
                    addressDetailVm.stateOrProvinceName(),
                    addressDetailVm.countryId(),
                    addressDetailVm.countryName(),
                    userAddress.getIsActive()
                ))
            ).collect(Collectors.toList());

        //sort by isActive
        Comparator<ActiveAddressVm> comparator = Comparator.comparing(ActiveAddressVm::isActive).reversed();
        return addressActiveVms.stream().sorted(comparator).collect(Collectors.toList());
    }

    public AddressDetailVm getAddressDefault() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals("anonymousUser")) {
            throw new AccessDeniedException(Constants.ErrorCode.UNAUTHENTICATED);
        }

        UserAddress userAddress = userAddressRepository.findByUserIdAndIsActiveTrue(userId).orElseThrow(()
            -> new NotFoundException(Constants.ErrorCode.USER_ADDRESS_NOT_FOUND));

        return locationService.getAddressById(userAddress.getAddressId());
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
            throw new NotFoundException(Constants.ErrorCode.USER_ADDRESS_NOT_FOUND);
        }
        userAddressRepository.delete(userAddress);
    }

    public void chooseDefaultAddress(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
        for (UserAddress userAddress : userAddressList) {
            userAddress.setIsActive(Objects.equals(userAddress.getAddressId(), id));
        }
        userAddressRepository.saveAll(userAddressList);
    }
}
