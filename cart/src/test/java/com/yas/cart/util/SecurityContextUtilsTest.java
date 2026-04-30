package com.yas.cart.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class SecurityContextUtilsTest {

    @Test
    void testConstructorIsPrivate() throws Exception {
        // Dùng Reflection để gọi Constructor private - giúp tăng 1 line coverage (dòng 12-13)
        Constructor<SecurityContextUtils> constructor = SecurityContextUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        
        SecurityContextUtils instance = constructor.newInstance();
        assertNotNull(instance);
    }
    
    @Test
    void testSetUpSecurityContext_Success() {
        String testUser = "test-user-123";
        
        // Gọi hàm static
        SecurityContextUtils.setUpSecurityContext(testUser);
        
        // Kiểm tra kết quả để đảm bảo code bên trong đã chạy (Line Coverage)
        assertNotNull(SecurityContextHolder.getContext());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(testUser, SecurityContextHolder.getContext().getAuthentication().getName());
        
        // Clear context sau khi test xong
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSecurityContextUtils_FullCoverage() throws Exception {
        // BƯỚC 1: Phủ Constructor (Dòng 12-13)
        Constructor<SecurityContextUtils> constructor = SecurityContextUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        SecurityContextUtils instance = constructor.newInstance();
        assertNotNull(instance);

        // BƯỚC 2: Phủ static method (Dòng 15-21)
        String user = "test-user";
        SecurityContextUtils.setUpSecurityContext(user);
        
        // Kiểm tra xem code có thực sự chạy qua các dòng gán context không
        assertNotNull(SecurityContextHolder.getContext());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getName());
        
        SecurityContextHolder.clearContext();
    }

    @Test
    void testConstructor() throws Exception {
        var constructor = SecurityContextUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance(); // Dòng này sẽ làm "xanh" cái khối trống bạn vừa hỏi
    }
}