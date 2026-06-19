package com.yas.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
    }

    private void mockUser(String userId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(userId);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getUserAddressList_WhenAnonymous_ShouldThrowException() {
        mockUser("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getUserAddressList_WhenAuthenticated_ShouldReturnList() {
        mockUser("user1");
        UserAddress ua = UserAddress.builder().userId("user1").addressId(1L).isActive(true).build();
        when(userAddressRepository.findAllByUserId("user1")).thenReturn(List.of(ua));
        
        AddressDetailVm detail = new AddressDetailVm(1L, "name", "phone", "line1", "city", "zip", 1L, "district", 1L, "state", 1L, "country");
        when(locationService.getAddressesByIdList(List.of(1L))).thenReturn(List.of(detail));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();
        assertEquals(1, result.size());
        verify(userAddressRepository).findAllByUserId("user1");
    }

    @Test
    void getAddressDefault_WhenNotFound_ShouldThrowException() {
        mockUser("user1");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user1")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void createAddress_ShouldSaveAddress() {
        mockUser("user1");
        AddressPostVm postVm = new AddressPostVm("name", "phone", "line1", "city", "zip", 1L, 1L, 1L);
        AddressVm addressVm = new AddressVm(1L, "name", "phone", "line1", "city", "zip", 1L, 1L, 1L);
        
        when(userAddressRepository.findAllByUserId("user1")).thenReturn(List.of());
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(postVm);
        assertNotNull(result);
        verify(userAddressRepository).save(any());
    }

    @Test
    void deleteAddress_WhenNotFound_ShouldThrowException() {
        mockUser("user1");
        when(userAddressRepository.findOneByUserIdAndAddressId("user1", 1L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(1L));
    }

    @Test
    void chooseDefaultAddress_ShouldUpdateAll() {
        mockUser("user1");
        UserAddress ua = UserAddress.builder().userId("user1").addressId(1L).isActive(false).build();
        when(userAddressRepository.findAllByUserId("user1")).thenReturn(List.of(ua));

        userAddressService.chooseDefaultAddress(1L);
        verify(userAddressRepository).saveAll(any());
    }

    @Test
    void deleteAddress_WhenSuccess_ShouldDelete() {
        mockUser("user1");
        UserAddress ua = UserAddress.builder().userId("user1").addressId(1L).isActive(false).build();
        when(userAddressRepository.findOneByUserIdAndAddressId("user1", 1L)).thenReturn(ua);

        userAddressService.deleteAddress(1L);
        verify(userAddressRepository).delete(ua);
    }
}
