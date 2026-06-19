package com.yas.customer.service;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private final String VALID_USER_ID = "user-123";
    private final String ANONYMOUS_USER = "anonymousUser";

    @BeforeEach
    void setUp() {
        // Thiết lập SecurityContext giả cho mọi bài test
        SecurityContextHolder.setContext(securityContext);
    }

    // Hàm tiện ích để giả lập user đăng nhập
    private void mockLoginUser(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
    }

    @Test
    void getUserAddressList_WhenAnonymousUser_ThrowsAccessDeniedException() {
        mockLoginUser(ANONYMOUS_USER);
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getAddressDefault_WhenAnonymousUser_ThrowsAccessDeniedException() {
        mockLoginUser(ANONYMOUS_USER);
        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_WhenAddressNotFound_ThrowsNotFoundException() {
        mockLoginUser(VALID_USER_ID);
        when(userAddressRepository.findByUserIdAndIsActiveTrue(VALID_USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_WhenSuccess_ReturnsAddressDetail() {
        mockLoginUser(VALID_USER_ID);
        UserAddress mockUserAddress = new UserAddress();
        mockUserAddress.setAddressId(1L);

        when(userAddressRepository.findByUserIdAndIsActiveTrue(VALID_USER_ID)).thenReturn(Optional.of(mockUserAddress));
        
        // Dùng mock vì AddressDetailVm có thể là record hoặc class phức tạp
        AddressDetailVm mockDetail = mock(AddressDetailVm.class);
        when(locationService.getAddressById(1L)).thenReturn(mockDetail);

        AddressDetailVm result = userAddressService.getAddressDefault();
        assertNotNull(result);
        verify(locationService).getAddressById(1L);
    }

    @Test
    void createAddress_Success() {
        mockLoginUser(VALID_USER_ID);
        AddressPostVm mockPostVm = mock(AddressPostVm.class);
        AddressVm mockAddressVm = mock(AddressVm.class);
        
        when(mockAddressVm.id()).thenReturn(1L); // Giả lập id trả về là 1
        when(userAddressRepository.findAllByUserId(VALID_USER_ID)).thenReturn(List.of()); // List rỗng = isFirstAddress true
        when(locationService.createAddress(mockPostVm)).thenReturn(mockAddressVm);

        UserAddress savedAddress = new UserAddress();
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

        assertDoesNotThrow(() -> userAddressService.createAddress(mockPostVm));
        verify(userAddressRepository).save(any(UserAddress.class));
    }

    @Test
    void deleteAddress_WhenNotFound_ThrowsNotFoundException() {
        mockLoginUser(VALID_USER_ID);
        when(userAddressRepository.findOneByUserIdAndAddressId(VALID_USER_ID, 1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(1L));
    }

    @Test
    void deleteAddress_Success() {
        mockLoginUser(VALID_USER_ID);
        UserAddress mockUserAddress = new UserAddress();
        when(userAddressRepository.findOneByUserIdAndAddressId(VALID_USER_ID, 1L)).thenReturn(mockUserAddress);

        userAddressService.deleteAddress(1L);
        verify(userAddressRepository).delete(mockUserAddress);
    }

    @Test
    void chooseDefaultAddress_Success() {
        mockLoginUser(VALID_USER_ID);
        UserAddress address1 = new UserAddress();
        address1.setAddressId(1L);
        UserAddress address2 = new UserAddress();
        address2.setAddressId(2L);

        when(userAddressRepository.findAllByUserId(VALID_USER_ID)).thenReturn(List.of(address1, address2));

        userAddressService.chooseDefaultAddress(1L);

        assertTrue(address1.getIsActive()); // Địa chỉ 1 được chọn làm mặc định
        assertFalse(address2.getIsActive()); // Địa chỉ 2 bị hủy mặc định
        verify(userAddressRepository).saveAll(anyList());
    }
}