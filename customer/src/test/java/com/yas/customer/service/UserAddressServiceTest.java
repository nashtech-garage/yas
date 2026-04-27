package com.yas.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import org.junit.jupiter.api.Nested;
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

    private static final String USER_ID = "user-123";
    private static final Long ADDRESS_ID = 10L;

    private void mockAuthenticatedUser(String userId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userId);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    class GetUserAddressListTest {

        @Test
        void testGetUserAddressList_whenUserIsAnonymous_shouldThrowAccessDeniedException() {
            mockAuthenticatedUser("anonymousUser");

            assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
        }

        @Test
        void testGetUserAddressList_whenUserHasAddresses_shouldReturnSortedByActiveFirst() {
            mockAuthenticatedUser(USER_ID);

            UserAddress activeAddress = UserAddress.builder()
                .id(1L).userId(USER_ID).addressId(ADDRESS_ID).isActive(true).build();
            UserAddress inactiveAddress = UserAddress.builder()
                .id(2L).userId(USER_ID).addressId(20L).isActive(false).build();

            AddressDetailVm activeDetail = AddressDetailVm.builder()
                .id(ADDRESS_ID).contactName("Alice").phone("111")
                .addressLine1("Street A").city("City A").zipCode("11111")
                .districtId(1L).districtName("D1").stateOrProvinceId(1L)
                .stateOrProvinceName("SP1").countryId(1L).countryName("Country1").build();
            AddressDetailVm inactiveDetail = AddressDetailVm.builder()
                .id(20L).contactName("Bob").phone("222")
                .addressLine1("Street B").city("City B").zipCode("22222")
                .districtId(2L).districtName("D2").stateOrProvinceId(2L)
                .stateOrProvinceName("SP2").countryId(2L).countryName("Country2").build();

            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(inactiveAddress, activeAddress));
            when(locationService.getAddressesByIdList(anyList()))
                .thenReturn(List.of(activeDetail, inactiveDetail));

            List<ActiveAddressVm> result = userAddressService.getUserAddressList();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.get(0).isActive());
        }

        @Test
        void testGetUserAddressList_whenUserHasNoAddresses_shouldReturnEmptyList() {
            mockAuthenticatedUser(USER_ID);

            when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
            when(locationService.getAddressesByIdList(anyList())).thenReturn(List.of());

            List<ActiveAddressVm> result = userAddressService.getUserAddressList();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetAddressDefaultTest {

        @Test
        void testGetAddressDefault_whenUserIsAnonymous_shouldThrowAccessDeniedException() {
            mockAuthenticatedUser("anonymousUser");

            assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
        }

        @Test
        void testGetAddressDefault_whenNoActiveAddress_shouldThrowNotFoundException() {
            mockAuthenticatedUser(USER_ID);

            when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
                .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
        }

        @Test
        void testGetAddressDefault_whenActiveAddressExists_shouldReturnAddressDetail() {
            mockAuthenticatedUser(USER_ID);

            UserAddress activeAddress = UserAddress.builder()
                .id(1L).userId(USER_ID).addressId(ADDRESS_ID).isActive(true).build();
            AddressDetailVm addressDetailVm = AddressDetailVm.builder()
                .id(ADDRESS_ID).contactName("Alice").phone("111")
                .addressLine1("Street A").city("City A").zipCode("11111")
                .districtId(1L).districtName("D1").stateOrProvinceId(1L)
                .stateOrProvinceName("SP1").countryId(1L).countryName("Country1").build();

            when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
                .thenReturn(Optional.of(activeAddress));
            when(locationService.getAddressById(ADDRESS_ID)).thenReturn(addressDetailVm);

            AddressDetailVm result = userAddressService.getAddressDefault();

            assertNotNull(result);
            assertEquals(ADDRESS_ID, result.id());
            assertEquals("Alice", result.contactName());
        }
    }

    @Nested
    class CreateAddressTest {

        private AddressPostVm addressPostVm;

        @BeforeEach
        void setUp() {
            addressPostVm = new AddressPostVm(
                "Alice", "111", "Street A", "City A", "11111", 1L, 1L, 1L);
        }

        @Test
        void testCreateAddress_whenFirstAddress_shouldSetIsActiveTrue() {
            mockAuthenticatedUser(USER_ID);

            AddressVm addressVm = AddressVm.builder()
                .id(ADDRESS_ID).contactName("Alice").phone("111")
                .addressLine1("Street A").city("City A").zipCode("11111")
                .districtId(1L).stateOrProvinceId(1L).countryId(1L).build();
            UserAddress savedAddress = UserAddress.builder()
                .id(1L).userId(USER_ID).addressId(ADDRESS_ID).isActive(true).build();

            when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
            when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);
            when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

            UserAddressVm result = userAddressService.createAddress(addressPostVm);

            assertNotNull(result);
            assertEquals(USER_ID, result.userId());
            assertTrue(result.isActive());
            verify(userAddressRepository).save(any(UserAddress.class));
        }

        @Test
        void testCreateAddress_whenNotFirstAddress_shouldSetIsActiveFalse() {
            mockAuthenticatedUser(USER_ID);

            UserAddress existingAddress = UserAddress.builder()
                .id(1L).userId(USER_ID).addressId(5L).isActive(true).build();
            AddressVm addressVm = AddressVm.builder()
                .id(ADDRESS_ID).contactName("Alice").phone("111")
                .addressLine1("Street A").city("City A").zipCode("11111")
                .districtId(1L).stateOrProvinceId(1L).countryId(1L).build();
            UserAddress savedAddress = UserAddress.builder()
                .id(2L).userId(USER_ID).addressId(ADDRESS_ID).isActive(false).build();

            when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(existingAddress));
            when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);
            when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

            UserAddressVm result = userAddressService.createAddress(addressPostVm);

            assertNotNull(result);
            assertTrue(!result.isActive());
        }
    }

    @Nested
    class DeleteAddressTest {

        @Test
        void testDeleteAddress_whenAddressNotFound_shouldThrowNotFoundException() {
            mockAuthenticatedUser(USER_ID);

            when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID))
                .thenReturn(null);

            assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(ADDRESS_ID));
            verify(userAddressRepository, never()).delete(any());
        }

        @Test
        void testDeleteAddress_whenAddressExists_shouldDeleteSuccessfully() {
            mockAuthenticatedUser(USER_ID);

            UserAddress userAddress = UserAddress.builder()
                .id(1L).userId(USER_ID).addressId(ADDRESS_ID).isActive(false).build();
            when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID))
                .thenReturn(userAddress);

            userAddressService.deleteAddress(ADDRESS_ID);

            verify(userAddressRepository).delete(userAddress);
        }
    }

    @Nested
    class ChooseDefaultAddressTest {

        @Test
        void testChooseDefaultAddress_whenCalled_shouldSetOnlySelectedAddressActive() {
            mockAuthenticatedUser(USER_ID);

            UserAddress address1 = UserAddress.builder()
                .id(1L).userId(USER_ID).addressId(ADDRESS_ID).isActive(false).build();
            UserAddress address2 = UserAddress.builder()
                .id(2L).userId(USER_ID).addressId(20L).isActive(true).build();

            when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(address1, address2));
            when(userAddressRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

            userAddressService.chooseDefaultAddress(ADDRESS_ID);

            verify(userAddressRepository).saveAll(anyList());
            assertTrue(address1.getIsActive());
            assertTrue(!address2.getIsActive());
        }

        @Test
        void testChooseDefaultAddress_whenNoAddresses_shouldSaveEmptyList() {
            mockAuthenticatedUser(USER_ID);

            when(userAddressRepository.findAllByUserId(anyString())).thenReturn(List.of());
            when(userAddressRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

            userAddressService.chooseDefaultAddress(ADDRESS_ID);

            verify(userAddressRepository).saveAll(List.of());
        }
    }
}
