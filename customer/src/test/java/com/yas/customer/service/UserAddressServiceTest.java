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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAddressServiceTest {

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);

        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserAddressList_whenAnonymousUser_throwAccessDeniedException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("anonymousUser");

        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getUserAddressList_whenUserHasAddresses_thenReturnSortedList() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        UserAddress address1 = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(true).build();
        UserAddress address2 = UserAddress.builder().id(2L).userId("user-1").addressId(20L).isActive(false).build();
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(address1, address2));

        AddressDetailVm detail1 = new AddressDetailVm(10L, "John", "123", "Line1", "City1", "Zip1", 1L, "Dist1", 2L, "State1", 3L, "Country1");
        AddressDetailVm detail2 = new AddressDetailVm(20L, "Jane", "456", "Line2", "City2", "Zip2", 1L, "Dist2", 2L, "State2", 3L, "Country2");
        when(locationService.getAddressesByIdList(List.of(10L, 20L))).thenReturn(List.of(detail1, detail2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(0).id()).isEqualTo(10L);
        assertThat(result.get(1).isActive()).isFalse();
        assertThat(result.get(1).id()).isEqualTo(20L);
    }

    @Test
    void getAddressDefault_whenAnonymousUser_throwAccessDeniedException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("anonymousUser");

        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_whenAddressNotFound_throwNotFoundException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_whenAddressExists_thenReturnAddressDetail() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        UserAddress address1 = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(true).build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1")).thenReturn(Optional.of(address1));

        AddressDetailVm detail1 = new AddressDetailVm(10L, "John", "123", "Line1", "City1", "Zip1", 1L, "Dist1", 2L, "State1", 3L, "Country1");
        when(locationService.getAddressById(10L)).thenReturn(detail1);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.contactName()).isEqualTo("John");
    }

    @Test
    void createAddress_whenFirstAddress_thenMakeItActive() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of());

        AddressPostVm postVm = new AddressPostVm("John", "123", "Line1", "City1", "Zip1", 1L, 2L, 3L);
        AddressVm addressVm = new AddressVm(10L, "John", "123", "Line1", "City1", "Zip1", 1L, 2L, 3L);
        when(locationService.createAddress(postVm)).thenReturn(addressVm);

        UserAddress savedAddress = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(true).build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.isActive()).isTrue();

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsActive()).isTrue();
    }

    @Test
    void createAddress_whenNotFirstAddress_thenMakeItInactive() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        UserAddress existingAddress = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(true).build();
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(existingAddress));

        AddressPostVm postVm = new AddressPostVm("Jane", "456", "Line2", "City2", "Zip2", 1L, 2L, 3L);
        AddressVm addressVm = new AddressVm(20L, "Jane", "456", "Line2", "City2", "Zip2", 1L, 2L, 3L);
        when(locationService.createAddress(postVm)).thenReturn(addressVm);

        UserAddress savedAddress = UserAddress.builder().id(2L).userId("user-1").addressId(20L).isActive(false).build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.isActive()).isFalse();

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsActive()).isFalse();
    }

    @Test
    void deleteAddress_whenNotFound_throwNotFoundException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 10L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(10L));
    }

    @Test
    void deleteAddress_whenFound_thenDelete() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        UserAddress address = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(true).build();
        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 10L)).thenReturn(address);

        userAddressService.deleteAddress(10L);

        verify(userAddressRepository).delete(address);
    }

    @Test
    void chooseDefaultAddress_thenSetOthersToInactive() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-1");

        UserAddress address1 = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(true).build();
        UserAddress address2 = UserAddress.builder().id(2L).userId("user-1").addressId(20L).isActive(false).build();
        
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(address1, address2));

        userAddressService.chooseDefaultAddress(20L);

        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        
        List<UserAddress> savedList = captor.getValue();
        assertThat(savedList).hasSize(2);
        
        // address 10L should now be inactive
        UserAddress savedAddress1 = savedList.stream().filter(a -> a.getAddressId().equals(10L)).findFirst().get();
        assertThat(savedAddress1.getIsActive()).isFalse();
        
        // address 20L should now be active
        UserAddress savedAddress2 = savedList.stream().filter(a -> a.getAddressId().equals(20L)).findFirst().get();
        assertThat(savedAddress2.getIsActive()).isTrue();
    }
}
