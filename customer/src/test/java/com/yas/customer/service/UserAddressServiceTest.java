package com.yas.customer.service;

import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class UserAddressServiceTest {

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    private static final String USER_ID = "test-user-id";

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
    }

    private void setUpAnonymousUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("anonymousUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    private UserAddress createUserAddress(Long id, String userId, Long addressId, Boolean isActive) {
        return UserAddress.builder()
            .id(id)
            .userId(userId)
            .addressId(addressId)
            .isActive(isActive)
            .build();
    }

    private AddressDetailVm createAddressDetailVm(Long id) {
        return new AddressDetailVm(
            id,
            "John Doe",
            "+1234567890",
            "123 Elm Street",
            "Springfield",
            "62701",
            101L,
            "Downtown",
            201L,
            "Illinois",
            301L,
            "United States"
        );
    }

    // ==================== getUserAddressList ====================

    @Nested
    class GetUserAddressListTests {

        @Test
        void getUserAddressList_whenAuthenticated_returnSortedActiveAddresses() {
            setUpSecurityContext(USER_ID);

            UserAddress address1 = createUserAddress(1L, USER_ID, 10L, false);
            UserAddress address2 = createUserAddress(2L, USER_ID, 20L, true);

            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(address1, address2));

            AddressDetailVm detail1 = createAddressDetailVm(10L);
            AddressDetailVm detail2 = createAddressDetailVm(20L);

            when(locationService.getAddressesByIdList(List.of(10L, 20L)))
                .thenReturn(List.of(detail1, detail2));

            List<ActiveAddressVm> result = userAddressService.getUserAddressList();

            assertThat(result).hasSize(2);
            // Active address should be first (sorted by isActive descending)
            assertThat(result.get(0).isActive()).isTrue();
            assertThat(result.get(1).isActive()).isFalse();
        }

        @Test
        void getUserAddressList_whenNoAddresses_returnEmptyList() {
            setUpSecurityContext(USER_ID);

            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(Collections.emptyList());
            when(locationService.getAddressesByIdList(anyList()))
                .thenReturn(Collections.emptyList());

            List<ActiveAddressVm> result = userAddressService.getUserAddressList();

            assertThat(result).isEmpty();
        }

        @Test
        void getUserAddressList_whenAnonymousUser_throwAccessDeniedException() {
            setUpAnonymousUser();

            assertThrows(AccessDeniedException.class,
                () -> userAddressService.getUserAddressList());
        }
    }

    // ==================== getAddressDefault ====================

    @Nested
    class GetAddressDefaultTests {

        @Test
        void getAddressDefault_whenActiveAddressExists_returnAddressDetailVm() {
            setUpSecurityContext(USER_ID);

            UserAddress activeAddress = createUserAddress(1L, USER_ID, 10L, true);
            when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
                .thenReturn(Optional.of(activeAddress));

            AddressDetailVm expectedDetail = createAddressDetailVm(10L);
            when(locationService.getAddressById(10L)).thenReturn(expectedDetail);

            AddressDetailVm result = userAddressService.getAddressDefault();

            assertThat(result).isEqualTo(expectedDetail);
            assertThat(result.id()).isEqualTo(10L);
        }

        @Test
        void getAddressDefault_whenNoActiveAddress_throwNotFoundException() {
            setUpSecurityContext(USER_ID);

            when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
                .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> userAddressService.getAddressDefault());
        }

        @Test
        void getAddressDefault_whenAnonymousUser_throwAccessDeniedException() {
            setUpAnonymousUser();

            assertThrows(AccessDeniedException.class,
                () -> userAddressService.getAddressDefault());
        }
    }

    // ==================== createAddress ====================

    @Nested
    class CreateAddressTests {

        @Test
        void createAddress_whenFirstAddress_shouldSetActiveTrue() {
            setUpSecurityContext(USER_ID);

            // No existing addresses
            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(Collections.emptyList());

            AddressPostVm postVm = new AddressPostVm(
                "Jane Smith", "+1987654321", "456 Oak Avenue",
                "Metropolis", "54321", 102L, 202L, 302L
            );
            AddressVm createdAddress = AddressVm.builder()
                .id(100L)
                .contactName("Jane Smith")
                .phone("+1987654321")
                .addressLine1("456 Oak Avenue")
                .city("Metropolis")
                .zipCode("54321")
                .districtId(102L)
                .stateOrProvinceId(202L)
                .countryId(302L)
                .build();

            when(locationService.createAddress(postVm)).thenReturn(createdAddress);

            UserAddress savedAddress = createUserAddress(1L, USER_ID, 100L, true);
            when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

            UserAddressVm result = userAddressService.createAddress(postVm);

            assertThat(result).isNotNull();
            assertThat(result.isActive()).isTrue();
            assertThat(result.userId()).isEqualTo(USER_ID);
        }

        @Test
        void createAddress_whenExistingAddresses_shouldSetActiveFalse() {
            setUpSecurityContext(USER_ID);

            // Has existing addresses
            UserAddress existingAddress = createUserAddress(1L, USER_ID, 10L, true);
            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(existingAddress));

            AddressPostVm postVm = new AddressPostVm(
                "Jane Smith", "+1987654321", "456 Oak Avenue",
                "Metropolis", "54321", 102L, 202L, 302L
            );
            AddressVm createdAddress = AddressVm.builder()
                .id(200L)
                .contactName("Jane Smith")
                .phone("+1987654321")
                .addressLine1("456 Oak Avenue")
                .city("Metropolis")
                .zipCode("54321")
                .districtId(102L)
                .stateOrProvinceId(202L)
                .countryId(302L)
                .build();

            when(locationService.createAddress(postVm)).thenReturn(createdAddress);

            UserAddress savedAddress = createUserAddress(2L, USER_ID, 200L, false);
            when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

            UserAddressVm result = userAddressService.createAddress(postVm);

            assertThat(result).isNotNull();
            assertThat(result.isActive()).isFalse();
        }
    }

    // ==================== deleteAddress ====================

    @Nested
    class DeleteAddressTests {

        @Test
        void deleteAddress_whenAddressExists_shouldDeleteSuccessfully() {
            setUpSecurityContext(USER_ID);

            UserAddress userAddress = createUserAddress(1L, USER_ID, 10L, false);
            when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 10L))
                .thenReturn(userAddress);

            userAddressService.deleteAddress(10L);

            verify(userAddressRepository).delete(userAddress);
        }

        @Test
        void deleteAddress_whenAddressNotFound_throwNotFoundException() {
            setUpSecurityContext(USER_ID);

            when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 999L))
                .thenReturn(null);

            assertThrows(NotFoundException.class,
                () -> userAddressService.deleteAddress(999L));
        }
    }

    // ==================== chooseDefaultAddress ====================

    @Nested
    class ChooseDefaultAddressTests {

        @Test
        void chooseDefaultAddress_whenCalled_shouldSetOnlySelectedAsActive() {
            setUpSecurityContext(USER_ID);

            UserAddress address1 = createUserAddress(1L, USER_ID, 10L, true);
            UserAddress address2 = createUserAddress(2L, USER_ID, 20L, false);
            UserAddress address3 = createUserAddress(3L, USER_ID, 30L, false);

            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(address1, address2, address3));

            // Choose address with addressId = 20L as default
            userAddressService.chooseDefaultAddress(20L);

            // address1 (addressId=10L) should now be false
            assertThat(address1.getIsActive()).isFalse();
            // address2 (addressId=20L) should now be true
            assertThat(address2.getIsActive()).isTrue();
            // address3 (addressId=30L) should remain false
            assertThat(address3.getIsActive()).isFalse();

            verify(userAddressRepository).saveAll(List.of(address1, address2, address3));
        }

        @Test
        void chooseDefaultAddress_whenNoMatchingAddress_shouldSetAllInactive() {
            setUpSecurityContext(USER_ID);

            UserAddress address1 = createUserAddress(1L, USER_ID, 10L, true);
            UserAddress address2 = createUserAddress(2L, USER_ID, 20L, false);

            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(address1, address2));

            // Choose non-existent addressId
            userAddressService.chooseDefaultAddress(999L);

            assertThat(address1.getIsActive()).isFalse();
            assertThat(address2.getIsActive()).isFalse();

            verify(userAddressRepository).saveAll(List.of(address1, address2));
        }
    }
}
