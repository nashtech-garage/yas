package com.yas.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.AfterEach;
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

    @BeforeEach
    void setUp() {
        // Gắn SecurityContext giả vào hệ thống trước mỗi bài test
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Dọn dẹp để không ảnh hưởng các test khác
        SecurityContextHolder.clearContext();
    }

    // Hàm tiện ích để dễ dàng thay đổi tên user đăng nhập
    private void mockSecurityContext(String username) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(username);
    }

    // ==========================================
    // 1. TESTS CHO HÀM: getUserAddressList
    // ==========================================

    @Test
    void getUserAddressList_WhenAnonymousUser_ShouldThrowAccessDeniedException() {
        mockSecurityContext("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getUserAddressList_Success() {
        mockSecurityContext("user123");

        UserAddress userAddress1 = UserAddress.builder().id(1L).userId("user123").addressId(10L).isActive(false).build();
        UserAddress userAddress2 = UserAddress.builder().id(2L).userId("user123").addressId(20L).isActive(true).build();
        when(userAddressRepository.findAllByUserId("user123")).thenReturn(List.of(userAddress1, userAddress2));

        AddressDetailVm addressDetail1 = mock(AddressDetailVm.class);
        lenient().when(addressDetail1.id()).thenReturn(10L);
        lenient().when(addressDetail1.contactName()).thenReturn("Name 1");

        AddressDetailVm addressDetail2 = mock(AddressDetailVm.class);
        lenient().when(addressDetail2.id()).thenReturn(20L);
        lenient().when(addressDetail2.contactName()).thenReturn("Name 2");

        when(locationService.getAddressesByIdList(List.of(10L, 20L))).thenReturn(List.of(addressDetail1, addressDetail2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Cần đảm bảo list đã sort: isActive = true phải nằm lên đầu tiên
        assertTrue(result.get(0).isActive());
        assertEquals(20L, result.get(0).id());
        assertFalse(result.get(1).isActive());
        assertEquals(10L, result.get(1).id());
    }

    // ==========================================
    // 2. TESTS CHO HÀM: getAddressDefault
    // ==========================================

    @Test
    void getAddressDefault_WhenAnonymousUser_ShouldThrowAccessDeniedException() {
        mockSecurityContext("anonymousUser");
        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_WhenNotFound_ShouldThrowNotFoundException() {
        mockSecurityContext("user123");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_Success() {
        mockSecurityContext("user123");
        UserAddress userAddress = UserAddress.builder().id(1L).userId("user123").addressId(10L).isActive(true).build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user123")).thenReturn(Optional.of(userAddress));

        AddressDetailVm expectedAddress = mock(AddressDetailVm.class);
        lenient().when(expectedAddress.id()).thenReturn(10L);
        when(locationService.getAddressById(10L)).thenReturn(expectedAddress);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertNotNull(result);
        assertEquals(10L, result.id());
    }

    // ==========================================
    // 3. TESTS CHO HÀM: createAddress
    // ==========================================

    @Test
    void createAddress_FirstAddress_ShouldSetActiveTrue() {
        mockSecurityContext("user123");
        // Giả lập danh sách rỗng để đánh vào nhánh isFirstAddress = true
        when(userAddressRepository.findAllByUserId("user123")).thenReturn(List.of()); 

        AddressPostVm postVm = mock(AddressPostVm.class);
        AddressVm addressGetVm = mock(AddressVm.class);
        when(addressGetVm.id()).thenReturn(10L);
        when(locationService.createAddress(postVm)).thenReturn(addressGetVm);

        when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(i -> i.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertNotNull(result);
        
        // Kiểm tra xem trường isActive có được set = true hay không
        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsActive());
    }

    @Test
    void createAddress_NotFirstAddress_ShouldSetActiveFalse() {
        mockSecurityContext("user123");
        UserAddress existingAddress = UserAddress.builder().id(1L).build();
        // Giả lập danh sách đã có data để đánh vào nhánh isFirstAddress = false
        when(userAddressRepository.findAllByUserId("user123")).thenReturn(List.of(existingAddress)); 

        AddressPostVm postVm = mock(AddressPostVm.class);
        AddressVm addressGetVm = mock(AddressVm.class);
        when(addressGetVm.id()).thenReturn(10L);
        when(locationService.createAddress(postVm)).thenReturn(addressGetVm);

        when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(i -> i.getArgument(0));

        userAddressService.createAddress(postVm);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertFalse(captor.getValue().getIsActive());
    }

    // ==========================================
    // 4. TESTS CHO HÀM: deleteAddress
    // ==========================================

    @Test
    void deleteAddress_WhenNotFound_ShouldThrowNotFoundException() {
        mockSecurityContext("user123");
        when(userAddressRepository.findOneByUserIdAndAddressId("user123", 10L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(10L));
    }

    @Test
    void deleteAddress_Success() {
        mockSecurityContext("user123");
        UserAddress userAddress = UserAddress.builder().id(1L).build();
        when(userAddressRepository.findOneByUserIdAndAddressId("user123", 10L)).thenReturn(userAddress);

        userAddressService.deleteAddress(10L);

        verify(userAddressRepository).delete(userAddress);
    }

    // ==========================================
    // 5. TESTS CHO HÀM: chooseDefaultAddress
    // ==========================================

    @Test
    void chooseDefaultAddress_Success() {
        mockSecurityContext("user123");
        UserAddress address1 = UserAddress.builder().id(1L).addressId(10L).isActive(false).build();
        UserAddress address2 = UserAddress.builder().id(2L).addressId(20L).isActive(true).build();
        when(userAddressRepository.findAllByUserId("user123")).thenReturn(List.of(address1, address2));

        // Act: Đổi default sang cái 10L
        userAddressService.chooseDefaultAddress(10L);

        // Assert: Kỳ vọng address1 = true, address2 = false
        assertTrue(address1.getIsActive());
        assertFalse(address2.getIsActive());
        verify(userAddressRepository).saveAll(List.of(address1, address2));
    }
}