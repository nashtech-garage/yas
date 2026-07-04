package com.yas.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class UserAddressServiceTest {

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    private SecurityContext securityContext;
    private Authentication authentication;

    private static final String USER_ID = "test-user-id";
    private static final String ANONYMOUS_USER = "anonymousUser";

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);

        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockSecurityContext(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
    }

    @Test
    void getUserAddressList_WhenUserIsAnonymous_ThrowsAccessDeniedException() {
        mockSecurityContext(ANONYMOUS_USER);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
        assertEquals(Constants.ErrorCode.UNAUTHENTICATED, exception.getMessage());
    }

    @Test
    void getUserAddressList_WhenValidUser_ReturnsSortedActiveAddressVmList() {
        mockSecurityContext(USER_ID);

        UserAddress userAddress1 = new UserAddress();
        userAddress1.setAddressId(1L);
        userAddress1.setIsActive(false);

        UserAddress userAddress2 = new UserAddress();
        userAddress2.setAddressId(2L);
        userAddress2.setIsActive(true);

        List<UserAddress> userAddressList = List.of(userAddress1, userAddress2);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddressList);

        AddressDetailVm addressDetailVm1 = new AddressDetailVm(1L, "Contact 1", "1234", "Line 1", "City 1", "1000", 1L, "Dist 1", 1L, "State 1", 1L, "Country 1");
        AddressDetailVm addressDetailVm2 = new AddressDetailVm(2L, "Contact 2", "5678", "Line 2", "City 2", "2000", 2L, "Dist 2", 2L, "State 2", 2L, "Country 2");
        when(locationService.getAddressesByIdList(anyList())).thenReturn(List.of(addressDetailVm1, addressDetailVm2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertEquals(2, result.size());
        assertTrue(result.get(0).isActive()); // Active address should be sorted first
        assertEquals(2L, result.get(0).id());
        assertEquals(1L, result.get(1).id());
    }

    @Test
    void getAddressDefault_WhenUserIsAnonymous_ThrowsAccessDeniedException() {
        mockSecurityContext(ANONYMOUS_USER);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
        assertEquals(Constants.ErrorCode.UNAUTHENTICATED, exception.getMessage());
    }

    @Test
    void getAddressDefault_WhenAddressNotFound_ThrowsNotFoundException() {
        mockSecurityContext(USER_ID);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
        assertEquals("User address not found", exception.getMessage());
    }

    @Test
    void getAddressDefault_WhenValidUserAndAddressExists_ReturnsAddressDetailVm() {
        mockSecurityContext(USER_ID);

        UserAddress userAddress = new UserAddress();
        userAddress.setAddressId(1L);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(userAddress));

        AddressDetailVm addressDetailVm = new AddressDetailVm(1L, "Contact", "123", "Line", "City", "Zip", 1L, "Dist", 1L, "State", 1L, "Country");
        when(locationService.getAddressById(1L)).thenReturn(addressDetailVm);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void createAddress_WhenFirstAddress_SetsIsActiveTrue() {
        mockSecurityContext(USER_ID);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());

        AddressPostVm addressPostVm = new AddressPostVm("Contact", "123", "Line", "City", "Zip", 1L, 1L, 1L);
        AddressVm addressVm = new AddressVm(1L, "Contact", "123", "Line", "City", "Zip", 1L, 1L, 1L);
        when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);

        UserAddress savedAddress = UserAddress.builder().userId(USER_ID).addressId(1L).isActive(true).build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

        UserAddressVm result = userAddressService.createAddress(addressPostVm);

        assertNotNull(result);
        assertTrue(result.isActive());
        
        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsActive());
    }

    @Test
    void createAddress_WhenNotFirstAddress_SetsIsActiveFalse() {
        mockSecurityContext(USER_ID);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(new UserAddress()));

        AddressPostVm addressPostVm = new AddressPostVm("Contact", "123", "Line", "City", "Zip", 1L, 1L, 1L);
        AddressVm addressVm = new AddressVm(2L, "Contact", "123", "Line", "City", "Zip", 1L, 1L, 1L);
        when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);

        UserAddress savedAddress = UserAddress.builder().userId(USER_ID).addressId(2L).isActive(false).build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

        userAddressService.createAddress(addressPostVm);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertEquals(false, captor.getValue().getIsActive());
    }

    @Test
    void deleteAddress_WhenAddressNotFound_ThrowsNotFoundException() {
        mockSecurityContext(USER_ID);
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(1L));
        assertEquals("User address not found", exception.getMessage());
    }

    @Test
    void deleteAddress_WhenValidAddress_DeletesAddress() {
        mockSecurityContext(USER_ID);
        UserAddress userAddress = new UserAddress();
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(userAddress);

        userAddressService.deleteAddress(1L);

        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void chooseDefaultAddress_UpdatesAddressesCorrectly() {
        mockSecurityContext(USER_ID);
        
        UserAddress userAddress1 = new UserAddress();
        userAddress1.setAddressId(1L);
        userAddress1.setIsActive(false);

        UserAddress userAddress2 = new UserAddress();
        userAddress2.setAddressId(2L);
        userAddress2.setIsActive(true);

        List<UserAddress> userAddressList = List.of(userAddress1, userAddress2);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddressList);

        userAddressService.chooseDefaultAddress(1L);

        assertTrue(userAddress1.getIsActive());
        assertEquals(false, userAddress2.getIsActive());

        verify(userAddressRepository).saveAll(userAddressList);
    }
}
