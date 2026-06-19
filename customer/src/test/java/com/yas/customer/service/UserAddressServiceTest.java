package com.yas.customer.service;

import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserAddressList_WhenAnonymous_ShouldThrowAccessDeniedException() {
        setUpSecurityContext("anonymousUser");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> userAddressService.getUserAddressList());

        assertEquals(Constants.ErrorCode.UNAUTHENTICATED, exception.getMessage());
    }

    @Test
    void getUserAddressList_ShouldReturnMappedAndSortedByActiveDesc() {
        setUpSecurityContext("user1");

        UserAddress inactiveAddress = UserAddress.builder().id(1L).userId("user1").addressId(11L).isActive(false).build();
        UserAddress activeAddress = UserAddress.builder().id(2L).userId("user1").addressId(22L).isActive(true).build();
        when(userAddressRepository.findAllByUserId("user1")).thenReturn(List.of(inactiveAddress, activeAddress));

        AddressDetailVm first = new AddressDetailVm(11L, "A", "1", "line1", "city1", "zip1", 1L, "district1", 2L, "state1", 3L, "country1");
        AddressDetailVm second = new AddressDetailVm(22L, "B", "2", "line2", "city2", "zip2", 4L, "district2", 5L, "state2", 6L, "country2");
        when(locationService.getAddressesByIdList(List.of(11L, 22L))).thenReturn(List.of(first, second));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertEquals(2, result.size());
        assertTrue(result.getFirst().isActive());
        assertFalse(result.get(1).isActive());
        assertEquals(22L, result.getFirst().id());
        assertEquals(11L, result.get(1).id());
    }

    @Test
    void getAddressDefault_WhenAnonymous_ShouldThrowAccessDeniedException() {
        setUpSecurityContext("anonymousUser");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> userAddressService.getAddressDefault());

        assertEquals(Constants.ErrorCode.UNAUTHENTICATED, exception.getMessage());
    }

    @Test
    void getAddressDefault_WhenNoActiveAddress_ShouldThrowNotFoundException() {
        setUpSecurityContext("user1");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user1")).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userAddressService.getAddressDefault());

        assertEquals("User address not found", exception.getMessage());
    }

    @Test
    void getAddressDefault_WhenFound_ShouldReturnAddressDetail() {
        setUpSecurityContext("user1");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user1"))
                .thenReturn(java.util.Optional.of(UserAddress.builder().addressId(99L).build()));
        AddressDetailVm expected = new AddressDetailVm(99L, "A", "1", "line", "city", "zip", 1L, "district", 2L, "state", 3L, "country");
        when(locationService.getAddressById(99L)).thenReturn(expected);

        AddressDetailVm actual = userAddressService.getAddressDefault();

        assertEquals(expected, actual);
    }

    @Test
    void createAddress_WhenFirstAddress_ShouldSaveAsActive() {
        setUpSecurityContext("user1");
        when(userAddressRepository.findAllByUserId("user1")).thenReturn(List.of());

        AddressPostVm request = new AddressPostVm("A", "1", "line", "city", "zip", 1L, 2L, 3L);
        AddressVm addressVm = new AddressVm(100L, "A", "1", "line", "city", "zip", 1L, 2L, 3L);
        when(locationService.createAddress(request)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(invocation -> {
            UserAddress model = invocation.getArgument(0);
            model.setId(5L);
            return model;
        });

        UserAddressVm result = userAddressService.createAddress(request);

        assertEquals("user1", result.userId());
        assertEquals(100L, result.addressGetVm().id());
        assertTrue(result.isActive());
    }

    @Test
    void createAddress_WhenNotFirstAddress_ShouldSaveAsInactive() {
        setUpSecurityContext("user1");
        when(userAddressRepository.findAllByUserId("user1"))
                .thenReturn(List.of(UserAddress.builder().id(1L).userId("user1").addressId(1L).isActive(true).build()));

        AddressPostVm request = new AddressPostVm("A", "1", "line", "city", "zip", 1L, 2L, 3L);
        AddressVm addressVm = new AddressVm(200L, "A", "1", "line", "city", "zip", 1L, 2L, 3L);
        when(locationService.createAddress(request)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(request);

        assertFalse(result.isActive());
    }

    @Test
    void deleteAddress_WhenNotFound_ShouldThrowNotFoundException() {
        setUpSecurityContext("user1");
        when(userAddressRepository.findOneByUserIdAndAddressId("user1", 10L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userAddressService.deleteAddress(10L));

        assertEquals("User address not found", exception.getMessage());
    }

    @Test
    void deleteAddress_WhenFound_ShouldDelete() {
        setUpSecurityContext("user1");
        UserAddress existing = UserAddress.builder().id(1L).userId("user1").addressId(10L).isActive(true).build();
        when(userAddressRepository.findOneByUserIdAndAddressId("user1", 10L)).thenReturn(existing);

        userAddressService.deleteAddress(10L);

        verify(userAddressRepository).delete(existing);
    }

    @Test
    void chooseDefaultAddress_ShouldUpdateActiveFlagsAndSaveAll() {
        setUpSecurityContext("user1");
        UserAddress first = UserAddress.builder().id(1L).userId("user1").addressId(10L).isActive(true).build();
        UserAddress second = UserAddress.builder().id(2L).userId("user1").addressId(20L).isActive(false).build();
        when(userAddressRepository.findAllByUserId("user1")).thenReturn(List.of(first, second));

        userAddressService.chooseDefaultAddress(20L);

        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        List<UserAddress> saved = captor.getValue();
        assertFalse(saved.get(0).getIsActive());
        assertTrue(saved.get(1).getIsActive());
    }
}
