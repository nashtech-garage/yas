package com.yas.payment.util; 

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.payment.utils.MessagesUtils; 

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils; 

class MessagesUtilsTest {

@BeforeEach
    void setUp() {
        ResourceBundle dummyBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                if ("EXISTING_KEY".equals(key)) {
                    return "Message found with arg: {}";
                }
                return null;
            }

            @Override
            public Enumeration<String> getKeys() {
                Vector<String> keys = new Vector<>();
                keys.add("EXISTING_KEY");
                return keys.elements();
            }
        };

        ReflectionTestUtils.setField(MessagesUtils.class, "messageBundle", dummyBundle);
    }

    @Test
    void constructor_shouldBeInvoked() {
        MessagesUtils utils = new MessagesUtils();
        assertNotNull(utils);
    }

    @Test
    void getMessage_shouldReturnFormattedMessage_whenKeyExists() {
        String result = MessagesUtils.getMessage("EXISTING_KEY", "Success");
        assertEquals("Message found with arg: Success", result);
    }

    @Test
    void getMessage_shouldReturnFormattedErrorCode_whenKeyDoesNotExist() {
        String result = MessagesUtils.getMessage("KEY_NOT_FOUND_WITH_ARG_{}", "Fallback");
        assertEquals("KEY_NOT_FOUND_WITH_ARG_Fallback", result);
    }
}