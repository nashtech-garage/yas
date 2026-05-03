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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAddressServiceTest {

    @Mock
    UserAddressRepository userAddressRepository;
    @Mock
    LocationService locationService;

    @InjectMocks
    UserAddressService userAddressService;

    Authentication authentication;

    @BeforeEach
    void setUp() {
        // Giả lập Security Context để vượt qua chốt chặn lấy UserId
        SecurityContext securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        // Mặc định cho userId là "test-user"
        org.mockito.Mockito.lenient().when(authentication.getName()).thenReturn("test-user");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==========================================
    // TEST: getUserAddressList
    // ==========================================
    @Test
    void testGetUserAddressList_Unauthenticated() {
        when(authentication.getName()).thenReturn("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void testGetUserAddressList_Success() {
        // Mock UserAddress entity
        UserAddress userAddress1 = UserAddress.builder().userId("test-user").addressId(1L).isActive(false).build();
        UserAddress userAddress2 = UserAddress.builder().userId("test-user").addressId(2L).isActive(true).build();
        when(userAddressRepository.findAllByUserId("test-user")).thenReturn(List.of(userAddress1, userAddress2));

        // Mock AddressDetailVm (Dùng Record hoặc class có các hàm tự động sinh)
        AddressDetailVm detailVm1 = mock(AddressDetailVm.class);
        lenient().when(detailVm1.id()).thenReturn(1L);
        lenient().when(detailVm1.contactName()).thenReturn("John");
        
        AddressDetailVm detailVm2 = mock(AddressDetailVm.class);
        lenient().when(detailVm2.id()).thenReturn(2L);
        lenient().when(detailVm2.contactName()).thenReturn("Jane");

        when(locationService.getAddressesByIdList(anyList())).thenReturn(List.of(detailVm1, detailVm2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertNotNull(result);
        assertEquals(2, result.size());
        // Do hàm có lẹnh sort by isActive reversed, thằng active(true) sẽ nằm trên cùng
        assertTrue(result.get(0).isActive()); 
    }

    // ==========================================
    // TEST: getAddressDefault
    // ==========================================
    @Test
    void testGetAddressDefault_Unauthenticated() {
        when(authentication.getName()).thenReturn("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testGetAddressDefault_NotFound() {
        when(userAddressRepository.findByUserIdAndIsActiveTrue(anyString())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testGetAddressDefault_Success() {
        UserAddress userAddress = UserAddress.builder().userId("test-user").addressId(1L).isActive(true).build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue("test-user")).thenReturn(Optional.of(userAddress));
        
        AddressDetailVm detailVm = mock(AddressDetailVm.class);
        when(locationService.getAddressById(1L)).thenReturn(detailVm);

        AddressDetailVm result = userAddressService.getAddressDefault();
        assertNotNull(result);
    }

    // ==========================================
    // TEST: createAddress
    // ==========================================
    @Test
    void testCreateAddress_FirstAddress_Success() {
        AddressPostVm postVm = mock(AddressPostVm.class);
        
        // Trả về list rỗng => Đây là địa chỉ đầu tiên => isActive = true
        when(userAddressRepository.findAllByUserId("test-user")).thenReturn(Collections.emptyList());
        
        AddressVm addressVm = mock(AddressVm.class);
        lenient().when(addressVm.id()).thenReturn(1L);
        when(locationService.createAddress(postVm)).thenReturn(addressVm);

        UserAddress savedEntity = UserAddress.builder().userId("test-user").addressId(1L).isActive(true).build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedEntity);

        UserAddressVm result = userAddressService.createAddress(postVm);
        assertNotNull(result);
    }

    @Test
    void testCreateAddress_NotFirstAddress_Success() {
        AddressPostVm postVm = mock(AddressPostVm.class);
        
        // Trả về list có phần tử => Không phải địa chỉ đầu tiên => isActive = false
        UserAddress existing = UserAddress.builder().build();
        when(userAddressRepository.findAllByUserId("test-user")).thenReturn(List.of(existing));
        
        AddressVm addressVm = mock(AddressVm.class);
        lenient().when(addressVm.id()).thenReturn(2L);
        when(locationService.createAddress(postVm)).thenReturn(addressVm);

        UserAddress savedEntity = UserAddress.builder().userId("test-user").addressId(2L).isActive(false).build();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedEntity);

        UserAddressVm result = userAddressService.createAddress(postVm);
        assertNotNull(result);
    }

    // ==========================================
    // TEST: deleteAddress
    // ==========================================
    @Test
    void testDeleteAddress_NotFound() {
        when(userAddressRepository.findOneByUserIdAndAddressId("test-user", 1L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(1L));
    }

    @Test
    void testDeleteAddress_Success() {
        UserAddress userAddress = UserAddress.builder().userId("test-user").addressId(1L).build();
        when(userAddressRepository.findOneByUserIdAndAddressId("test-user", 1L)).thenReturn(userAddress);

        userAddressService.deleteAddress(1L);
        verify(userAddressRepository).delete(userAddress);
    }

    // ==========================================
    // TEST: chooseDefaultAddress
    // ==========================================
    @Test
    void testChooseDefaultAddress_Success() {
        UserAddress addr1 = UserAddress.builder().addressId(1L).isActive(false).build();
        UserAddress addr2 = UserAddress.builder().addressId(2L).isActive(true).build();
        
        when(userAddressRepository.findAllByUserId("test-user")).thenReturn(List.of(addr1, addr2));

        // Hàm này sẽ đổi addr1 thành active (vì trùng ID 1L) và addr2 thành inactive
        userAddressService.chooseDefaultAddress(1L);

        assertTrue(addr1.getIsActive());
        assertFalse(addr2.getIsActive());
        verify(userAddressRepository).saveAll(anyList());
    }
}