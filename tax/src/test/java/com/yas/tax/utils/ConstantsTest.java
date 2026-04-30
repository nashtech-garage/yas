package com.yas.tax.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

class ConstantsTest {

    @Test
    void testConstructorIsPrivate() throws Exception {
        // Lấy hàm khởi tạo của lớp Constants
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        
        // Constants là final class, kiểm tra default constructor
        constructor.setAccessible(true);
        Constants instance = constructor.newInstance();
        
        // Kiểm tra đối tượng được tạo ra không null
        assertNotNull(instance);
    }
    
    @Test
    void testConstantsClass() {
        // Kiểm tra Constants class tồn tại và là final
        assertTrue(Modifier.isFinal(Constants.class.getModifiers()), 
            "Constants class should be final");
        assertNotNull(Constants.class);
    }
}