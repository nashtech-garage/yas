package com.yas.payment.util; 

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.payment.utils.Constants; 

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void constructor_Constants_shouldBeInvoked() {
        Constants constants = new Constants();
        assertNotNull(constants);
    }

    @Test
    void constructor_ErrorCode_shouldBeInvokedViaReflection() throws Exception {
        Constructor<Constants.ErrorCode> constructor = 
            Constants.ErrorCode.class.getDeclaredConstructor(Constants.class);
        
        constructor.setAccessible(true);
        Constants.ErrorCode errorCode = constructor.newInstance(new Constants());
        
        assertNotNull(errorCode);
    }

    @Test
    void constructor_Message_shouldBeInvoked() {
        Constants constants = new Constants();
        Constants.Message message = constants.new Message();
        assertNotNull(message);
    }

    @Test
    void verifyConstantValues() {
        assertEquals("PAYMENT_PROVIDER_NOT_FOUND", Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
        assertEquals("SUCCESS", Constants.Message.SUCCESS_MESSAGE);
    }
}