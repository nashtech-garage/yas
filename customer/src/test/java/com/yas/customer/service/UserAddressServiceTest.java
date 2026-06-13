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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAddressServiceTest {

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private UserAddressService userAddressService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private static final String USER_ID = "test-user-id";
    private static final Long ADDRESS_ID_1 = 1L;
    private static final Long ADDRESS_ID_2 = 2L;
    private static final String CONTACT_NAME = "John Doe";
    private static final String PHONE = "1234567890";
    private static final String ADDRESS_LINE = "123 Main St";
    private static final String CITY = "New York";
    private static final String ZIP_CODE = "10001";
    private static final Long DISTRICT_ID = 1L;
    private static final String DISTRICT_NAME = "Manhattan";
    private static final Long STATE_ID = 1L;
    private static final String STATE_NAME = "New York";
    private static final Long COUNTRY_ID = 1L;
    private static final String COUNTRY_NAME = "USA";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
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
        return AddressDetailVm.builder()
                .id(id)
                .contactName(CONTACT_NAME)
                .phone(PHONE)
                .addressLine1(ADDRESS_LINE)
                .city(CITY)
                .zipCode(ZIP_CODE)
                .districtId(DISTRICT_ID)
                .districtName(DISTRICT_NAME)
                .stateOrProvinceId(STATE_ID)
                .stateOrProvinceName(STATE_NAME)
                .countryId(COUNTRY_ID)
                .countryName(COUNTRY_NAME)
                .build();
    }

    private AddressVm createAddressVm(Long id) {
        return AddressVm.builder()
                .id(id)
                .contactName(CONTACT_NAME)
                .phone(PHONE)
                .addressLine1(ADDRESS_LINE)
                .city(CITY)
                .zipCode(ZIP_CODE)
                .districtId(DISTRICT_ID)
                .stateOrProvinceId(STATE_ID)
                .countryId(COUNTRY_ID)
                .build();
    }

    private AddressPostVm createAddressPostVm() {
        return new AddressPostVm(
                CONTACT_NAME,
                PHONE,
                ADDRESS_LINE,
                CITY,
                ZIP_CODE,
                DISTRICT_ID,
                STATE_ID,
                COUNTRY_ID
        );
    }

    @Test
    void testGetUserAddressList_whenUserAuthenticated_thenReturnAddressList() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        List<UserAddress> userAddressList = List.of(userAddress1, userAddress2);

        AddressDetailVm addressDetailVm1 = createAddressDetailVm(ADDRESS_ID_1);
        AddressDetailVm addressDetailVm2 = createAddressDetailVm(ADDRESS_ID_2);
        List<AddressDetailVm> addressDetailVmList = List.of(addressDetailVm1, addressDetailVm2);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddressList);
        when(locationService.getAddressesByIdList(anyList())).thenReturn(addressDetailVmList);

        // When
        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).isActive()).isTrue(); // Active address should be first
        assertThat(result.get(1).isActive()).isFalse();
        verify(userAddressRepository).findAllByUserId(USER_ID);
        verify(locationService).getAddressesByIdList(anyList());
    }

    @Test
    void testGetUserAddressList_whenUserNotAuthenticated_thenThrowAccessDeniedException() {
        // Given
        when(authentication.getName()).thenReturn("anonymousUser");

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> userAddressService.getUserAddressList());

        assertThat(exception.getMessage()).contains(Constants.ErrorCode.UNAUTHENTICATED);
        verify(userAddressRepository, never()).findAllByUserId(anyString());
    }

    @Test
    void testGetUserAddressList_whenNoAddresses_thenReturnEmptyList() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(locationService.getAddressesByIdList(anyList())).thenReturn(List.of());

        // When
        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAddressDefault_whenUserAuthenticatedAndHasDefaultAddress_thenReturnAddress() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        AddressDetailVm addressDetailVm = createAddressDetailVm(ADDRESS_ID_1);

        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
                .thenReturn(Optional.of(userAddress));
        when(locationService.getAddressById(ADDRESS_ID_1)).thenReturn(addressDetailVm);

        // When
        AddressDetailVm result = userAddressService.getAddressDefault();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(ADDRESS_ID_1);
        assertThat(result.contactName()).isEqualTo(CONTACT_NAME);
        verify(userAddressRepository).findByUserIdAndIsActiveTrue(USER_ID);
        verify(locationService).getAddressById(ADDRESS_ID_1);
    }

    @Test
    void testGetAddressDefault_whenUserNotAuthenticated_thenThrowAccessDeniedException() {
        // Given
        when(authentication.getName()).thenReturn("anonymousUser");

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> userAddressService.getAddressDefault());

        assertThat(exception.getMessage()).contains(Constants.ErrorCode.UNAUTHENTICATED);
        verify(userAddressRepository, never()).findByUserIdAndIsActiveTrue(anyString());
    }

    @Test
    void testGetAddressDefault_whenNoDefaultAddress_thenThrowNotFoundException() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userAddressService.getAddressDefault());

        assertThat(exception.getMessage()).contains("User address not found");
        verify(locationService, never()).getAddressById(anyLong());
    }

    @Test
    void testCreateAddress_whenFirstAddress_thenSetAsActive() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        AddressPostVm addressPostVm = createAddressPostVm();
        AddressVm addressVm = createAddressVm(ADDRESS_ID_1);
        UserAddress savedUserAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedUserAddress);

        // When
        UserAddressVm result = userAddressService.createAddress(addressPostVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isTrue(); // First address should be active
        assertThat(result.addressGetVm().id()).isEqualTo(ADDRESS_ID_1);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        UserAddress capturedUserAddress = captor.getValue();
        assertThat(capturedUserAddress.getUserId()).isEqualTo(USER_ID);
        assertThat(capturedUserAddress.getAddressId()).isEqualTo(ADDRESS_ID_1);
        assertThat(capturedUserAddress.getIsActive()).isTrue();
    }

    @Test
    void testCreateAddress_whenNotFirstAddress_thenSetAsInactive() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        AddressPostVm addressPostVm = createAddressPostVm();
        AddressVm addressVm = createAddressVm(ADDRESS_ID_2);
        UserAddress existingUserAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress savedUserAddress = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);

        when(userAddressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(existingUserAddress));
        when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedUserAddress);

        // When
        UserAddressVm result = userAddressService.createAddress(addressPostVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isFalse(); // Additional address should not be active
        assertThat(result.addressGetVm().id()).isEqualTo(ADDRESS_ID_2);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        UserAddress capturedUserAddress = captor.getValue();
        assertThat(capturedUserAddress.getIsActive()).isFalse();
    }

    @Test
    void testDeleteAddress_whenAddressExists_thenDeleteSuccessfully() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1))
                .thenReturn(userAddress);

        // When
        userAddressService.deleteAddress(ADDRESS_ID_1);

        // Then
        verify(userAddressRepository).findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1);
        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void testDeleteAddress_whenAddressNotFound_thenThrowNotFoundException() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1))
                .thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userAddressService.deleteAddress(ADDRESS_ID_1));

        assertThat(exception.getMessage()).contains("User address not found");
        verify(userAddressRepository, never()).delete(any(UserAddress.class));
    }

    @Test
    void testChooseDefaultAddress_whenAddressExists_thenUpdateActiveStatus() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        List<UserAddress> userAddressList = new ArrayList<>(List.of(userAddress1, userAddress2));

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddressList);

        // When
        userAddressService.chooseDefaultAddress(ADDRESS_ID_2);

        // Then
        verify(userAddressRepository).findAllByUserId(USER_ID);
        
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        
        List<UserAddress> savedAddresses = captor.getValue();
        assertThat(savedAddresses).hasSize(2);
        
        UserAddress savedAddress1 = savedAddresses.stream()
                .filter(addr -> addr.getAddressId().equals(ADDRESS_ID_1))
                .findFirst()
                .orElseThrow();
        assertThat(savedAddress1.getIsActive()).isFalse(); // Should be inactive now
        
        UserAddress savedAddress2 = savedAddresses.stream()
                .filter(addr -> addr.getAddressId().equals(ADDRESS_ID_2))
                .findFirst()
                .orElseThrow();
        assertThat(savedAddress2.getIsActive()).isTrue(); // Should be active now
    }

    @Test
    void testChooseDefaultAddress_whenSameAddressAlreadyActive_thenNoChange() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        List<UserAddress> userAddressList = new ArrayList<>(List.of(userAddress1, userAddress2));

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddressList);

        // When
        userAddressService.chooseDefaultAddress(ADDRESS_ID_1);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        
        List<UserAddress> savedAddresses = captor.getValue();
        UserAddress savedAddress1 = savedAddresses.stream()
                .filter(addr -> addr.getAddressId().equals(ADDRESS_ID_1))
                .findFirst()
                .orElseThrow();
        assertThat(savedAddress1.getIsActive()).isTrue(); // Should remain active
    }

    @Test
    void testChooseDefaultAddress_whenMultipleAddresses_thenOnlyOneIsActive() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);

        UserAddress userAddress1 = createUserAddress(1L, USER_ID, ADDRESS_ID_1, true);
        UserAddress userAddress2 = createUserAddress(2L, USER_ID, ADDRESS_ID_2, false);
        UserAddress userAddress3 = createUserAddress(3L, USER_ID, 3L, false);
        List<UserAddress> userAddressList = new ArrayList<>(List.of(userAddress1, userAddress2, userAddress3));

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddressList);

        // When
        userAddressService.chooseDefaultAddress(ADDRESS_ID_2);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        
        List<UserAddress> savedAddresses = captor.getValue();
        long activeCount = savedAddresses.stream()
                .filter(UserAddress::getIsActive)
                .count();
        
        assertThat(activeCount).isEqualTo(1); // Only one address should be active
        
        UserAddress activeAddress = savedAddresses.stream()
                .filter(UserAddress::getIsActive)
                .findFirst()
                .orElseThrow();
        assertThat(activeAddress.getAddressId()).isEqualTo(ADDRESS_ID_2);
    }
}
