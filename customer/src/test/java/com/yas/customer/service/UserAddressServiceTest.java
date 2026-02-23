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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserAddressServiceTest {

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private LocationService locationService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserAddressService userAddressService;

    private static final String USER_ID = "test-user-id";
    private static final Long ADDRESS_ID_1 = 1L;
    private static final Long ADDRESS_ID_2 = 2L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testGetUserAddressList_whenUserAuthenticated_returnAddressList() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        List<UserAddress> userAddresses = Arrays.asList(userAddress1, userAddress2);

        AddressDetailVm addressDetail1 = createAddressDetailVm(ADDRESS_ID_1, "Contact 1", "123 Street", "City 1");
        AddressDetailVm addressDetail2 = createAddressDetailVm(ADDRESS_ID_2, "Contact 2", "456 Avenue", "City 2");
        List<AddressDetailVm> addressDetails = Arrays.asList(addressDetail1, addressDetail2);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);
        when(locationService.getAddressesByIdList(anyList())).thenReturn(addressDetails);

        // When
        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).isActive()).isFalse();
        verify(userAddressRepository).findAllByUserId(USER_ID);
        verify(locationService).getAddressesByIdList(anyList());
    }

    @Test
    void testGetUserAddressList_whenUserIsAnonymous_throwAccessDeniedException() {
        // Given
        when(authentication.getName()).thenReturn("anonymousUser");

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> userAddressService.getUserAddressList());

        assertThat(exception.getMessage()).contains(Constants.ErrorCode.UNAUTHENTICATED);
        verify(userAddressRepository, never()).findAllByUserId(anyString());
    }

    @Test
    void testGetUserAddressList_whenNoAddresses_returnEmptyList() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());
        when(locationService.getAddressesByIdList(anyList())).thenReturn(Collections.emptyList());

        // When
        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAddressDefault_whenDefaultAddressExists_returnAddressDetail() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        UserAddress userAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        AddressDetailVm addressDetail = createAddressDetailVm(ADDRESS_ID_1, "Default Contact", "789 Road",
                "Default City");

        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(userAddress));
        when(locationService.getAddressById(ADDRESS_ID_1)).thenReturn(addressDetail);

        // When
        AddressDetailVm result = userAddressService.getAddressDefault();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(ADDRESS_ID_1);
        assertThat(result.contactName()).isEqualTo("Default Contact");
        verify(userAddressRepository).findByUserIdAndIsActiveTrue(USER_ID);
        verify(locationService).getAddressById(ADDRESS_ID_1);
    }

    @Test
    void testGetAddressDefault_whenUserIsAnonymous_throwAccessDeniedException() {
        // Given
        when(authentication.getName()).thenReturn("anonymousUser");

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> userAddressService.getAddressDefault());

        assertThat(exception.getMessage()).contains(Constants.ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void testGetAddressDefault_whenNoDefaultAddress_throwNotFoundException() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userAddressService.getAddressDefault());

        assertThat(exception.getMessage()).contains("User address not found");
    }

    @Test
    void testCreateAddress_whenFirstAddress_shouldSetAsActive() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        AddressPostVm addressPostVm = createAddressPostVm("New Contact", "New Street");
        AddressVm addressVm = new AddressVm(ADDRESS_ID_1, "Contact", "123456", "Address", "City", "12345", 1L, 1L, 1L);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());
        when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);

        UserAddress savedUserAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedUserAddress);

        // When
        UserAddressVm result = userAddressService.createAddress(addressPostVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isTrue();
        assertThat(result.addressGetVm().id()).isEqualTo(ADDRESS_ID_1);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsActive()).isTrue();
    }

    @Test
    void testCreateAddress_whenNotFirstAddress_shouldSetAsInactive() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        AddressPostVm addressPostVm = createAddressPostVm("New Contact", "New Street");
        AddressVm addressVm = new AddressVm(ADDRESS_ID_2, "Contact", "123456", "Address", "City", "12345", 1L, 1L, 1L);

        UserAddress existingAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(existingAddress));
        when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);

        UserAddress savedUserAddress = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedUserAddress);

        // When
        UserAddressVm result = userAddressService.createAddress(addressPostVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isFalse();

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsActive()).isFalse();
    }

    @Test
    void testDeleteAddress_whenAddressExists_shouldDelete() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        UserAddress userAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, false);

        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1)).thenReturn(userAddress);

        // When
        userAddressService.deleteAddress(ADDRESS_ID_1);

        // Then
        verify(userAddressRepository).findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1);
        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void testDeleteAddress_whenAddressNotFound_throwNotFoundException() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1)).thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userAddressService.deleteAddress(ADDRESS_ID_1));

        assertThat(exception.getMessage()).contains("User address not found");
        verify(userAddressRepository, never()).delete(any());
    }

    @Test
    void testChooseDefaultAddress_whenAddressExists_shouldUpdateActiveStatus() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        List<UserAddress> userAddresses = Arrays.asList(userAddress1, userAddress2);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);

        // When
        userAddressService.chooseDefaultAddress(ADDRESS_ID_2);

        // Then
        assertFalse(userAddress1.getIsActive());
        assertTrue(userAddress2.getIsActive());

        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        List<UserAddress> saved = captor.getValue();
        assertThat(saved).hasSize(2);
    }

    @Test
    void testChooseDefaultAddress_whenSameAddressAlreadyActive_shouldRemainActive() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        List<UserAddress> userAddresses = Arrays.asList(userAddress1, userAddress2);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);

        // When
        userAddressService.chooseDefaultAddress(ADDRESS_ID_1);

        // Then
        assertTrue(userAddress1.getIsActive());
        assertFalse(userAddress2.getIsActive());
        verify(userAddressRepository).saveAll(anyList());
    }

    @Test
    void testGetUserAddressList_whenAddressListSorted_shouldReturnActiveFirst() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, false);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, true);
        List<UserAddress> userAddresses = Arrays.asList(userAddress1, userAddress2);

        AddressDetailVm addressDetail1 = createAddressDetailVm(ADDRESS_ID_1, "Contact 1", "123 Street", "City 1");
        AddressDetailVm addressDetail2 = createAddressDetailVm(ADDRESS_ID_2, "Contact 2", "456 Avenue", "City 2");
        List<AddressDetailVm> addressDetails = Arrays.asList(addressDetail1, addressDetail2);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);
        when(locationService.getAddressesByIdList(anyList())).thenReturn(addressDetails);

        // When
        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        // Then
        assertThat(result).hasSize(2);
        // Active address should be first after sorting
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(0).id()).isEqualTo(ADDRESS_ID_2);
    }

    // Helper methods
    private UserAddress createUserAddress(Long id, String userId, Long addressId, Boolean isActive) {
        return UserAddress.builder()
                .id(id)
                .userId(userId)
                .addressId(addressId)
                .isActive(isActive)
                .build();
    }

    private AddressDetailVm createAddressDetailVm(Long id, String contactName, String addressLine1, String city) {
        return new AddressDetailVm(
                id,
                contactName,
                "1234567890",
                addressLine1,
                city,
                "12345",
                1L,
                "District",
                1L,
                "State",
                1L,
                "Country");
    }

    private AddressPostVm createAddressPostVm(String contactName, String addressLine1) {
        return new AddressPostVm(
                contactName,
                "1234567890",
                addressLine1,
                "City",
                "12345",
                1L,
                1L,
                1L);
    }
}
