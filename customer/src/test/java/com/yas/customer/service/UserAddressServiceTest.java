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

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetUserAddressList_whenAuthenticated_returnActiveAddressVmList() {
        setUpSecurityContext("testUser");
        UserAddress userAddress = UserAddress.builder().userId("testUser").addressId(1L).isActive(true).build();
        when(userAddressRepository.findAllByUserId("testUser")).thenReturn(List.of(userAddress));

        AddressDetailVm addressDetail = AddressDetailVm.builder()
            .id(1L)
            .contactName("John Doe")
            .phone("123456789")
            .addressLine1("Address 1")
            .city("City")
            .zipCode("10000")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();
        when(locationService.getAddressesByIdList(List.of(1L))).thenReturn(List.of(addressDetail));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().contactName()).isEqualTo("John Doe");
        assertThat(result.getFirst().isActive()).isTrue();
    }

    @Test
    void testGetUserAddressList_whenAnonymous_throwAccessDeniedException() {
        setUpSecurityContext("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void testGetAddressDefault_whenAuthenticatedAndAddressExists_returnAddressDetailVm() {
        setUpSecurityContext("testUser");
        UserAddress userAddress = UserAddress.builder().userId("testUser").addressId(1L).isActive(true).build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue("testUser")).thenReturn(Optional.of(userAddress));

        AddressDetailVm addressDetail = AddressDetailVm.builder().id(1L).build();
        when(locationService.getAddressById(1L)).thenReturn(addressDetail);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isEqualTo(addressDetail);
    }

    @Test
    void testGetAddressDefault_whenAnonymous_throwAccessDeniedException() {
        setUpSecurityContext("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testGetAddressDefault_whenNoDefaultAddress_throwNotFoundException() {
        setUpSecurityContext("testUser");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("testUser")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testCreateAddress_whenFirstAddress_shouldBeActive() {
        setUpSecurityContext("testUser");
        when(userAddressRepository.findAllByUserId("testUser")).thenReturn(List.of());
        
        AddressPostVm postVm = new AddressPostVm("John", "123", "Addr", "City", "100", 1L, 1L, 1L);
        AddressVm addressVm = AddressVm.builder().id(1L).build();
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        
        UserAddress savedUserAddress = UserAddress.builder().userId("testUser").addressId(1L).isActive(true).build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedUserAddress);

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.isActive()).isTrue();
        verify(userAddressRepository).save(any(UserAddress.class));
    }

    @Test
    void testDeleteAddress_whenAddressExists_shouldDelete() {
        setUpSecurityContext("testUser");
        UserAddress userAddress = UserAddress.builder().userId("testUser").addressId(1L).build();
        when(userAddressRepository.findOneByUserIdAndAddressId("testUser", 1L)).thenReturn(userAddress);

        userAddressService.deleteAddress(1L);

        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void testDeleteAddress_whenAddressNotFound_throwNotFoundException() {
        setUpSecurityContext("testUser");
        when(userAddressRepository.findOneByUserIdAndAddressId("testUser", 1L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(1L));
    }

    @Test
    void testChooseDefaultAddress_shouldUpdateIsActive() {
        setUpSecurityContext("testUser");
        UserAddress addr1 = UserAddress.builder().userId("testUser").addressId(1L).isActive(true).build();
        UserAddress addr2 = UserAddress.builder().userId("testUser").addressId(2L).isActive(false).build();
        when(userAddressRepository.findAllByUserId("testUser")).thenReturn(List.of(addr1, addr2));

        userAddressService.chooseDefaultAddress(2L);

        assertThat(addr1.getIsActive()).isFalse();
        assertThat(addr2.getIsActive()).isTrue();
        verify(userAddressRepository).saveAll(any());
    }
}
