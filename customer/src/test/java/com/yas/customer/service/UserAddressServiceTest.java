package com.yas.customer.service;

import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;

class UserAddressServiceTest {

    private static final String USER_ID = "user-1";

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    @BeforeEach
    void setUp() {
        userAddressRepository = org.mockito.Mockito.mock(UserAddressRepository.class);
        locationService = org.mockito.Mockito.mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserAddressList_whenAnonymousUser_thenThrowAccessDeniedException() {
        setUpSecurityContext("anonymousUser");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
            () -> userAddressService.getUserAddressList());

        assertThat(exception.getMessage()).contains("ACTION FAILED, PLEASE LOGIN");
    }

    @Test
    void getUserAddressList_whenHasData_thenReturnSortedByActive() {
        setUpSecurityContext(USER_ID);

        UserAddress inactiveAddress = UserAddress.builder()
            .userId(USER_ID)
            .addressId(10L)
            .isActive(false)
            .build();
        UserAddress activeAddress = UserAddress.builder()
            .userId(USER_ID)
            .addressId(20L)
            .isActive(true)
            .build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(inactiveAddress, activeAddress));

        AddressDetailVm detail1 = new AddressDetailVm(
            10L, "A", "1", "Line1", "City", "Zip", 1L, "District", 2L, "State", 3L, "Country"
        );
        AddressDetailVm detail2 = new AddressDetailVm(
            20L, "B", "2", "Line2", "City", "Zip", 1L, "District", 2L, "State", 3L, "Country"
        );
        when(locationService.getAddressesByIdList(List.of(10L, 20L))).thenReturn(List.of(detail1, detail2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(20L);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).id()).isEqualTo(10L);
        assertThat(result.get(1).isActive()).isFalse();
    }

    @Test
    void getAddressDefault_whenNoDefaultAddress_thenThrowNotFoundException() {
        setUpSecurityContext(USER_ID);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userAddressService.getAddressDefault());

        assertThat(exception.getMessage()).contains("User address not found");
    }

    @Test
    void getAddressDefault_whenHasDefaultAddress_thenReturnAddressDetailVm() {
        setUpSecurityContext(USER_ID);

        UserAddress activeAddress = UserAddress.builder()
            .userId(USER_ID)
            .addressId(99L)
            .isActive(true)
            .build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(activeAddress));

        AddressDetailVm expected = new AddressDetailVm(
            99L, "John", "0123", "Street", "City", "Zip", 1L, "District", 2L, "State", 3L, "Country"
        );
        when(locationService.getAddressById(99L)).thenReturn(expected);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void createAddress_whenFirstAddress_thenSavedAsActive() {
        setUpSecurityContext(USER_ID);

        AddressPostVm postVm = new AddressPostVm("John", "0123", "Street", "City", "Zip", 1L, 2L, 3L);
        AddressVm createdAddress = new AddressVm(123L, "John", "0123", "Street", "City", "Zip", 1L, 2L, 3L);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(locationService.createAddress(postVm)).thenReturn(createdAddress);

        UserAddress saved = UserAddress.builder()
            .id(1L)
            .userId(USER_ID)
            .addressId(123L)
            .isActive(true)
            .build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(saved);

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.isActive()).isTrue();
        assertThat(result.addressGetVm()).isEqualTo(createdAddress);
    }

    @Test
    void deleteAddress_whenAddressNotFound_thenThrowNotFoundException() {
        setUpSecurityContext(USER_ID);
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 11L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userAddressService.deleteAddress(11L));

        assertThat(exception.getMessage()).contains("User address not found");
    }

    @Test
    void deleteAddress_whenAddressExists_thenDeleteSuccessfully() {
        setUpSecurityContext(USER_ID);

        UserAddress existing = UserAddress.builder().id(8L).userId(USER_ID).addressId(11L).isActive(false).build();
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 11L)).thenReturn(existing);

        userAddressService.deleteAddress(11L);

        verify(userAddressRepository).delete(existing);
    }

    @Test
    void chooseDefaultAddress_whenAddressListExists_thenOnlyRequestedAddressIsActive() {
        setUpSecurityContext(USER_ID);

        UserAddress a1 = UserAddress.builder().id(1L).userId(USER_ID).addressId(11L).isActive(true).build();
        UserAddress a2 = UserAddress.builder().id(2L).userId(USER_ID).addressId(22L).isActive(false).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(a1, a2));

        userAddressService.chooseDefaultAddress(22L);

        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        List<UserAddress> savedList = captor.getValue();

        assertThat(savedList).hasSize(2);
        assertThat(savedList.stream().filter(UserAddress::getIsActive).map(UserAddress::getAddressId))
            .containsExactly(22L);
    }
}
