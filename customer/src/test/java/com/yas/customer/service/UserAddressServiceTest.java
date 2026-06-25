package com.yas.customer.service;

import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
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
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserAddressList_whenAnonymousUser_thenThrowAccessDeniedException() {
        setUpSecurityContext("anonymousUser");

        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getUserAddressList_whenAddressesExist_thenReturnActiveAddressListSortedByActive() {
        setUpSecurityContext(USER_ID);
        List<UserAddress> userAddresses = List.of(
            UserAddress.builder().id(1L).userId(USER_ID).addressId(10L).isActive(false).build(),
            UserAddress.builder().id(2L).userId(USER_ID).addressId(20L).isActive(true).build()
        );
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);
        when(locationService.getAddressesByIdList(List.of(10L, 20L))).thenReturn(List.of(
            addressDetailVm(10L, "Inactive User"),
            addressDetailVm(20L, "Active User")
        ));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().id()).isEqualTo(20L);
        assertThat(result.getFirst().isActive()).isTrue();
        assertThat(result.get(1).id()).isEqualTo(10L);
        assertThat(result.get(1).isActive()).isFalse();
    }

    @Test
    void getAddressDefault_whenAnonymousUser_thenThrowAccessDeniedException() {
        setUpSecurityContext("anonymousUser");

        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_whenNoDefaultAddress_thenThrowNotFoundException() {
        setUpSecurityContext(USER_ID);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_whenDefaultAddressExists_thenReturnAddressDetail() {
        setUpSecurityContext(USER_ID);
        UserAddress activeAddress = UserAddress.builder().id(1L).userId(USER_ID).addressId(20L).isActive(true).build();
        AddressDetailVm expected = addressDetailVm(20L, "Default User");
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(activeAddress));
        when(locationService.getAddressById(20L)).thenReturn(expected);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void createAddress_whenFirstAddress_thenSaveActiveAddress() {
        setUpSecurityContext(USER_ID);
        AddressPostVm request = addressPostVm();
        AddressVm createdAddress = addressVm(30L);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(locationService.createAddress(request)).thenReturn(createdAddress);
        when(userAddressRepository.save(org.mockito.ArgumentMatchers.any(UserAddress.class)))
            .thenAnswer(invocation -> {
                UserAddress saved = invocation.getArgument(0);
                saved.setId(99L);
                return saved;
            });

        UserAddressVm result = userAddressService.createAddress(request);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsActive()).isTrue();
        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.addressGetVm()).isEqualTo(createdAddress);
        assertThat(result.userId()).isEqualTo(USER_ID);
    }

    @Test
    void createAddress_whenNotFirstAddress_thenSaveInactiveAddress() {
        setUpSecurityContext(USER_ID);
        AddressPostVm request = addressPostVm();
        AddressVm createdAddress = addressVm(40L);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(
            UserAddress.builder().id(1L).userId(USER_ID).addressId(10L).isActive(true).build()
        ));
        when(locationService.createAddress(request)).thenReturn(createdAddress);
        when(userAddressRepository.save(org.mockito.ArgumentMatchers.any(UserAddress.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        userAddressService.createAddress(request);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsActive()).isFalse();
    }

    @Test
    void deleteAddress_whenAddressDoesNotExist_thenThrowNotFoundException() {
        setUpSecurityContext(USER_ID);
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 10L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(10L));
    }

    @Test
    void deleteAddress_whenAddressExists_thenDeleteAddress() {
        setUpSecurityContext(USER_ID);
        UserAddress userAddress = UserAddress.builder().id(1L).userId(USER_ID).addressId(10L).isActive(true).build();
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 10L)).thenReturn(userAddress);

        userAddressService.deleteAddress(10L);

        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void chooseDefaultAddress_whenAddressExists_thenUpdateActiveFlagsAndSaveAll() {
        setUpSecurityContext(USER_ID);
        UserAddress first = UserAddress.builder().id(1L).userId(USER_ID).addressId(10L).isActive(true).build();
        UserAddress second = UserAddress.builder().id(2L).userId(USER_ID).addressId(20L).isActive(false).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(first, second));

        userAddressService.chooseDefaultAddress(20L);

        assertThat(first.getIsActive()).isFalse();
        assertThat(second.getIsActive()).isTrue();
        verify(userAddressRepository).saveAll(anyList());
    }

    private AddressDetailVm addressDetailVm(Long id, String contactName) {
        return new AddressDetailVm(
            id,
            contactName,
            "0123456789",
            "Street 1",
            "City",
            "700000",
            1L,
            "District",
            2L,
            "Province",
            3L,
            "Country"
        );
    }

    private AddressPostVm addressPostVm() {
        return new AddressPostVm("John Doe", "0123456789", "Street 1", "City", "700000", 1L, 2L, 3L);
    }

    private AddressVm addressVm(Long id) {
        return new AddressVm(id, "John Doe", "0123456789", "Street 1", "City", "700000", 1L, 2L, 3L);
    }
}
