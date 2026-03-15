package com.yas.customer.service;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserAddressServiceTest {

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    private static final String USER_ID = "user-123";
    private static final Long ADDRESS_ID = 10L;

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
        // Set up authenticated security context
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList())
        );
    }

    // ── getUserAddressList ─────────────────────────────────────────────────────

    @Test
    void getUserAddressList_whenAuthenticated_returnsSortedByIsActiveDesc() {
        UserAddress inactive = UserAddress.builder().userId(USER_ID).addressId(1L).isActive(false).build();
        UserAddress active   = UserAddress.builder().userId(USER_ID).addressId(2L).isActive(true).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(inactive, active));

        AddressDetailVm detail1 = buildAddressDetailVm(1L);
        AddressDetailVm detail2 = buildAddressDetailVm(2L);
        when(locationService.getAddressesByIdList(List.of(1L, 2L))).thenReturn(List.of(detail1, detail2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        // active address must come first
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).isActive()).isFalse();
    }

    @Test
    void getUserAddressList_whenAnonymousUser_throwsAccessDeniedException() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList())
        );

        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
        verifyNoInteractions(userAddressRepository);
    }

    // ── getAddressDefault ──────────────────────────────────────────────────────

    @Test
    void getAddressDefault_whenActiveAddressExists_returnsAddressDetailVm() {
        UserAddress active = UserAddress.builder().userId(USER_ID).addressId(ADDRESS_ID).isActive(true).build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(active));

        AddressDetailVm expected = buildAddressDetailVm(ADDRESS_ID);
        when(locationService.getAddressById(ADDRESS_ID)).thenReturn(expected);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAddressDefault_whenNoActiveAddress_throwsNotFoundException() {
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
        verifyNoInteractions(locationService);
    }

    @Test
    void getAddressDefault_whenAnonymousUser_throwsAccessDeniedException() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList())
        );

        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    // ── createAddress ──────────────────────────────────────────────────────────

    @Test
    void createAddress_whenFirstAddress_isActiveShouldBeTrue() {
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());

        AddressPostVm postVm = buildAddressPostVm();
        AddressVm created = buildAddressVm(ADDRESS_ID);
        when(locationService.createAddress(postVm)).thenReturn(created);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        UserAddress saved = UserAddress.builder().userId(USER_ID).addressId(ADDRESS_ID).isActive(true).build();
        when(userAddressRepository.save(captor.capture())).thenReturn(saved);

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(captor.getValue().getIsActive()).isTrue();
        assertThat(result.addressGetVm().id()).isEqualTo(ADDRESS_ID);
    }

    @Test
    void createAddress_whenNotFirstAddress_isActiveShouldBeFalse() {
        UserAddress existing = UserAddress.builder().userId(USER_ID).addressId(1L).isActive(true).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(existing));

        AddressPostVm postVm = buildAddressPostVm();
        AddressVm created = buildAddressVm(ADDRESS_ID);
        when(locationService.createAddress(postVm)).thenReturn(created);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        UserAddress saved = UserAddress.builder().userId(USER_ID).addressId(ADDRESS_ID).isActive(false).build();
        when(userAddressRepository.save(captor.capture())).thenReturn(saved);

        userAddressService.createAddress(postVm);

        assertThat(captor.getValue().getIsActive()).isFalse();
    }

    // ── deleteAddress ──────────────────────────────────────────────────────────

    @Test
    void deleteAddress_whenAddressExists_deletesSuccessfully() {
        UserAddress userAddress = UserAddress.builder().userId(USER_ID).addressId(ADDRESS_ID).isActive(false).build();
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID)).thenReturn(userAddress);

        userAddressService.deleteAddress(ADDRESS_ID);

        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void deleteAddress_whenAddressNotFound_throwsNotFoundException() {
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(ADDRESS_ID));
        verify(userAddressRepository, never()).delete(any());
    }

    // ── chooseDefaultAddress ───────────────────────────────────────────────────

    @Test
    void chooseDefaultAddress_setsOnlySelectedAddressAsActive() {
        UserAddress addr1 = UserAddress.builder().userId(USER_ID).addressId(1L).isActive(true).build();
        UserAddress addr2 = UserAddress.builder().userId(USER_ID).addressId(2L).isActive(false).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(addr1, addr2));

        userAddressService.chooseDefaultAddress(2L);

        assertThat(addr1.getIsActive()).isFalse();
        assertThat(addr2.getIsActive()).isTrue();
        verify(userAddressRepository).saveAll(List.of(addr1, addr2));
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private AddressDetailVm buildAddressDetailVm(Long id) {
        return new AddressDetailVm(id, "John Doe", "123-456-7890", "123 Elm St",
            "Springfield", "12345", 101L, "District A", 10L, "State A", 1L, "Country A");
    }

    private AddressPostVm buildAddressPostVm() {
        return new AddressPostVm("John Doe", "123-456-7890", "123 Elm St",
            "Springfield", "12345", 101L, 10L, 1L);
    }

    private AddressVm buildAddressVm(Long id) {
        return AddressVm.builder()
            .id(id)
            .contactName("John Doe")
            .phone("123-456-7890")
            .addressLine1("123 Elm St")
            .city("Springfield")
            .zipCode("12345")
            .districtId(101L)
            .stateOrProvinceId(10L)
            .countryId(1L)
            .build();
    }
}
