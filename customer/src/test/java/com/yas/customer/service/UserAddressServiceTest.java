package com.yas.customer.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserAddressServiceTest {

    @Mock
    private UserAddressRepository userAddressRepository;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private UserAddressService userAddressService;

    @Test
    void getUserAddressList_whenAnonymous_shouldThrowAccessDenied() {
        setAuthenticationName("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getUserAddressList_whenValid_shouldReturnSortedByActive() {
        setAuthenticationName("user-1");
        UserAddress inactive = userAddress(1L, "user-1", 100L, false);
        UserAddress active = userAddress(2L, "user-1", 200L, true);
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(inactive, active));
        when(locationService.getAddressesByIdList(List.of(100L, 200L))).thenReturn(List.of(
                addressDetail(100L, "A"),
                addressDetail(200L, "B")));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().id()).isEqualTo(200L);
        assertThat(result.getFirst().isActive()).isTrue();
    }

    @Test
    void getAddressDefault_whenAnonymous_shouldThrowAccessDenied() {
        setAuthenticationName("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_whenNotFound_shouldThrowNotFound() {
        setAuthenticationName("user-1");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_whenFound_shouldReturnAddress() {
        setAuthenticationName("user-1");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1"))
                .thenReturn(Optional.of(userAddress(1L, "user-1", 100L, true)));
        when(locationService.getAddressById(100L)).thenReturn(addressDetail(100L, "A"));

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result.id()).isEqualTo(100L);
    }

    @Test
    void createAddress_whenFirstAddress_shouldSetActiveTrue() {
        setAuthenticationName("user-1");
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of());
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder().id(101L).build());
        when(userAddressRepository.save(any())).thenAnswer(invocation -> {
            UserAddress userAddress = invocation.getArgument(0);
            userAddress.setId(1L);
            return userAddress;
        });

        UserAddressVm result = userAddressService.createAddress(addressPost());

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void createAddress_whenNotFirst_shouldSetActiveFalse() {
        setAuthenticationName("user-1");
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(userAddress(1L, "user-1", 100L, true)));
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder().id(102L).build());
        when(userAddressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(addressPost());

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void deleteAddress_whenNotFound_shouldThrowNotFound() {
        setAuthenticationName("user-1");
        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 10L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(10L));
    }

    @Test
    void deleteAddress_whenFound_shouldDelete() {
        setAuthenticationName("user-1");
        UserAddress address = userAddress(1L, "user-1", 10L, true);
        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 10L)).thenReturn(address);

        userAddressService.deleteAddress(10L);

        verify(userAddressRepository).delete(address);
    }

    @Test
    void chooseDefaultAddress_shouldSetSelectedAddressActive() {
        setAuthenticationName("user-1");
        UserAddress first = userAddress(1L, "user-1", 10L, true);
        UserAddress second = userAddress(2L, "user-1", 20L, false);
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(first, second));

        userAddressService.chooseDefaultAddress(20L);

        assertThat(first.getIsActive()).isFalse();
        assertThat(second.getIsActive()).isTrue();
        verify(userAddressRepository).saveAll(List.of(first, second));
    }

    private static UserAddress userAddress(Long id, String userId, Long addressId, Boolean isActive) {
        return UserAddress.builder()
                .id(id)
                .userId(userId)
                .addressId(addressId)
                .isActive(isActive)
                .build();
    }

    private static AddressDetailVm addressDetail(Long id, String name) {
        return AddressDetailVm.builder()
                .id(id)
                .contactName(name)
                .phone("0909")
                .addressLine1("line1")
                .city("city")
                .zipCode("70000")
                .districtId(1L)
                .districtName("district")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("state")
                .countryId(3L)
                .countryName("country")
                .build();
    }

    private static AddressPostVm addressPost() {
        return new AddressPostVm("A", "0909", "line1", "city", "70000", 1L, 2L, 3L);
    }

    private static void setAuthenticationName(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }
}
