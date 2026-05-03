package com.yas.payment.utils;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstantsTest {

    @Test
    void testConstantsClasses() throws Exception {
        // Khởi tạo class cha Constants
        Constants constants = new Constants();
        assertNotNull(constants);

        // Khởi tạo class con Message
        Constants.Message message = constants.new Message();
        assertNotNull(message);

        // Dùng Java Reflection để ép khởi tạo ErrorCode (dù nó dùng private constructor)
        Constructor<Constants.ErrorCode> errorCodeConstructor = Constants.ErrorCode.class.getDeclaredConstructor(Constants.class);
        errorCodeConstructor.setAccessible(true);
        Constants.ErrorCode errorCode = errorCodeConstructor.newInstance(constants);
        assertNotNull(errorCode);

        // Assert các giá trị hằng số
        assertEquals("PAYMENT_PROVIDER_NOT_FOUND", Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
        assertEquals("SUCCESS", Constants.Message.SUCCESS_MESSAGE);
    }
}