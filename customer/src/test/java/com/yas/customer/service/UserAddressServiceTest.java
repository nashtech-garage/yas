package com.yas.customer.service;

import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

class UserAddressServiceTest {

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    private static final String USER_ID = "test-user";

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
    }

    @Test
    void getUserAddressList_Unauthenticated_ThrowsAccessDeniedException() {
        setUpSecurityContext("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getUserAddressList_Authenticated_ReturnsList() {
        setUpSecurityContext(USER_ID);
        UserAddress userAddress = UserAddress.builder()
            .userId(USER_ID)
            .addressId(1L)
            .isActive(true)
            .build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(userAddress));

        AddressDetailVm addressDetail = new AddressDetailVm(1L, "Contact", "123", "Line 1", "City", "Zip", 1L, "District", 1L, "State", 1L, "Country");
        when(locationService.getAddressesByIdList(List.of(1L))).thenReturn(List.of(addressDetail));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1L);
        assertThat(result.getFirst().isActive()).isTrue();
    }

    @Test
    void getAddressDefault_Unauthenticated_ThrowsAccessDeniedException() {
        setUpSecurityContext("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_NoActiveAddress_ThrowsNotFoundException() {
        setUpSecurityContext(USER_ID);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_HasActiveAddress_ReturnsAddress() {
        setUpSecurityContext(USER_ID);
        UserAddress userAddress = UserAddress.builder().addressId(1L).build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(userAddress));

        AddressDetailVm addressDetail = new AddressDetailVm(1L, "Contact", "123", "Line 1", "City", "Zip", 1L, "District", 1L, "State", 1L, "Country");
        when(locationService.getAddressById(1L)).thenReturn(addressDetail);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isEqualTo(addressDetail);
    }

    @Test
    void createAddress_IsFirstAddress_SetsActiveTrue() {
        setUpSecurityContext(USER_ID);
        AddressPostVm postVm = new AddressPostVm("Name", "Phone", "Line1", "City", "Zip", 1L, 1L, 1L);
        AddressVm addressVm = new AddressVm(1L, "Name", "Phone", "Line1", "City", "Zip", 1L, 1L, 1L);
        
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.isActive()).isTrue();
        assertThat(result.addressGetVm().id()).isEqualTo(1L);
    }

    @Test
    void createAddress_NotFirstAddress_SetsActiveFalse() {
        setUpSecurityContext(USER_ID);
        AddressPostVm postVm = new AddressPostVm("Name", "Phone", "Line1", "City", "Zip", 1L, 1L, 1L);
        AddressVm addressVm = new AddressVm(2L, "Name", "Phone", "Line1", "City", "Zip", 1L, 1L, 1L);
        
        UserAddress existing = UserAddress.builder().addressId(1L).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(existing));
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void deleteAddress_NotFound_ThrowsNotFoundException() {
        setUpSecurityContext(USER_ID);
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(1L));
    }

    @Test
    void deleteAddress_Found_Deletes() {
        setUpSecurityContext(USER_ID);
        UserAddress userAddress = UserAddress.builder().id(100L).build();
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(userAddress);

        userAddressService.deleteAddress(1L);

        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void chooseDefaultAddress_UpdatesIsActive() {
        setUpSecurityContext(USER_ID);
        UserAddress addr1 = UserAddress.builder().addressId(1L).isActive(true).build();
        UserAddress addr2 = UserAddress.builder().addressId(2L).isActive(false).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(addr1, addr2));

        userAddressService.chooseDefaultAddress(2L);

        assertThat(addr1.getIsActive()).isFalse();
        assertThat(addr2.getIsActive()).isTrue();
        verify(userAddressRepository).saveAll(any());
    }
}
