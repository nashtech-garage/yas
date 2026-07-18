package com.yas.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.util.SecurityContextUtils;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserAddressServiceTest {

    @Mock private UserAddressRepository userAddressRepository;
    @Mock private LocationService locationService;

    @InjectMocks private UserAddressService userAddressService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserAddressListShouldReturnActiveAddressFirst() {
        SecurityContextUtils.setUpSecurityContext("user-1");
        UserAddress inactive = userAddress(1L, 11L, false);
        UserAddress active = userAddress(2L, 22L, true);
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(inactive, active));
        when(locationService.getAddressesByIdList(List.of(11L, 22L))).thenReturn(List.of(
            addressDetail(11L, "Inactive"),
            addressDetail(22L, "Active")));

        var result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().id()).isEqualTo(22L);
        assertThat(result.getFirst().isActive()).isTrue();
    }

    @Test
    void getUserAddressListShouldRejectAnonymousUser() {
        SecurityContextUtils.setUpSecurityContext("anonymousUser");

        assertThatThrownBy(() -> userAddressService.getUserAddressList())
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getAddressDefaultShouldReturnActiveAddressOrThrow() {
        SecurityContextUtils.setUpSecurityContext("user-1");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1"))
            .thenReturn(Optional.of(userAddress(1L, 11L, true)));
        when(locationService.getAddressById(11L)).thenReturn(addressDetail(11L, "Default"));

        assertThat(userAddressService.getAddressDefault().id()).isEqualTo(11L);

        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userAddressService.getAddressDefault()).isInstanceOf(NotFoundException.class);
    }

    @Test
    void createDeleteAndChooseDefaultAddressShouldCoordinateRepositories() {
        SecurityContextUtils.setUpSecurityContext("user-1");
        AddressPostVm postVm = new AddressPostVm("Buyer", "0909", "Line 1", "HCM", "70000", 1L, 2L, 3L);
        when(userAddressRepository.findAllByUserId("user-1"))
            .thenReturn(List.of())
            .thenReturn(List.of(userAddress(1L, 11L, true), userAddress(2L, 22L, false)));
        when(locationService.createAddress(postVm)).thenReturn(addressVm(11L));
        when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 11L)).thenReturn(userAddress(1L, 11L, true));

        var created = userAddressService.createAddress(postVm);
        userAddressService.deleteAddress(11L);
        userAddressService.chooseDefaultAddress(22L);

        assertThat(created.id()).isNull();
        assertThat(created.addressGetVm().id()).isEqualTo(11L);
        assertThat(created.isActive()).isTrue();
        verify(userAddressRepository).delete(any(UserAddress.class));
        verify(userAddressRepository).saveAll(any());
    }

    @Test
    void deleteAddressShouldThrowWhenMappingMissing() {
        SecurityContextUtils.setUpSecurityContext("user-1");
        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 404L)).thenReturn(null);

        assertThatThrownBy(() -> userAddressService.deleteAddress(404L)).isInstanceOf(NotFoundException.class);
    }

    private UserAddress userAddress(Long id, Long addressId, Boolean active) {
        return UserAddress.builder().id(id).userId("user-1").addressId(addressId).isActive(active).build();
    }

    private AddressDetailVm addressDetail(Long id, String name) {
        return AddressDetailVm.builder()
            .id(id)
            .contactName(name)
            .phone("0909")
            .addressLine1("Line 1")
            .city("HCM")
            .zipCode("70000")
            .districtId(1L)
            .districtName("District")
            .stateOrProvinceId(2L)
            .stateOrProvinceName("State")
            .countryId(3L)
            .countryName("Vietnam")
            .build();
    }

    private AddressVm addressVm(Long id) {
        return AddressVm.builder()
            .id(id)
            .contactName("Buyer")
            .phone("0909")
            .addressLine1("Line 1")
            .city("HCM")
            .zipCode("70000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
    }
}
