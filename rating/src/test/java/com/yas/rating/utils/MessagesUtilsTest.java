package com.yas.rating.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    private ResourceBundle originalBundle;

    @BeforeEach
    void setUp() throws Exception {
        // Lấy field "messageBundle" trong class MessagesUtils
        Field field = MessagesUtils.class.getDeclaredField("messageBundle");
        field.setAccessible(true);
        
        // Lưu lại giá trị gốc để lát khôi phục
        originalBundle = (ResourceBundle) field.get(null);

        // Tạo một mock bundle chứa dữ liệu giả để test
        ResourceBundle mockBundle = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] {
                    {"SUCCESS_CODE", "Thao tác {} thành công {}!"}
                };
            }
        };
        
        // Bơm mock bundle vào class
        field.set(null, mockBundle);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Dọn dẹp: trả lại bundle gốc cho class sau khi test xong
        Field field = MessagesUtils.class.getDeclaredField("messageBundle");
        field.setAccessible(true);
        field.set(null, originalBundle);
    }

    @Test
    void constructor_Coverage() {
        // Phủ xanh (coverage) cho hàm khởi tạo mặc định của class
        MessagesUtils utils = new MessagesUtils();
        assertNotNull(utils);
    }

    @Test
    void getMessage_WhenErrorCodeExists_ShouldReturnFormattedMessage() {
        // Kịch bản 1: Key có tồn tại trong file bundle (Nhảy vào khối try)
        String result = MessagesUtils.getMessage("SUCCESS_CODE", "lưu", "100%");
        assertEquals("Thao tác lưu thành công 100%!", result);
    }

    @Test
    void getMessage_WhenErrorCodeMissing_ShouldReturnErrorCodeAsMessage() {
        // Kịch bản 2: Key không tồn tại, sẽ nhảy vào Exception (Khối catch)
        // Khi đó errorCode sẽ được dùng như một template string
        String result = MessagesUtils.getMessage("LỖI_RỒI: Không tìm thấy {}", "user");
        assertEquals("LỖI_RỒI: Không tìm thấy user", result);
    }
}