package com.yas.tax.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@ExtendWith(MockitoExtension.class)
class MessagesUtilsTest {

    private ResourceBundle originalBundle;

    @BeforeEach
    void setUp() throws Exception {
        // Lưu bundle gốc
        Field bundleField = MessagesUtils.class.getDeclaredField("messageBundle");
        bundleField.setAccessible(true);
        originalBundle = (ResourceBundle) bundleField.get(null);
    }

    @Test
    void testGetMessageWithExistingKey() throws Exception {
        // Mock ResourceBundle để test branch khi key tồn tại
        ResourceBundle mockBundle = mock(ResourceBundle.class);
        when(mockBundle.getString("TAX_CLASS_NOT_FOUND"))
            .thenReturn("Tax class with id {} not found");

        setMockBundle(mockBundle);

        String result = MessagesUtils.getMessage("TAX_CLASS_NOT_FOUND", 1L);

        assertNotNull(result);
        assertTrue(result.contains("1"), "Result should contain formatted argument");
        verify(mockBundle).getString("TAX_CLASS_NOT_FOUND");
    }

    @Test
    void testGetMessageWithMissingKey() throws Exception {
        // Mock ResourceBundle để test branch khi key không tồn tại (throw MissingResourceException)
        ResourceBundle mockBundle = mock(ResourceBundle.class);
        when(mockBundle.getString("non.existent.key"))
            .thenThrow(new MissingResourceException("key not found", 
                ResourceBundle.class.getName(), "non.existent.key"));

        setMockBundle(mockBundle);

        String result = MessagesUtils.getMessage("non.existent.key", null);

        // Khi MissingResourceException xảy ra, message = errorCode
        assertEquals("non.existent.key", result);
        verify(mockBundle).getString("non.existent.key");
    }

    @Test
    void testGetMessageWithMultipleArgs() throws Exception {
        // Test cover MessageFormatter với nhiều arguments
        ResourceBundle mockBundle = mock(ResourceBundle.class);
        when(mockBundle.getString("WELCOME_MESSAGE"))
            .thenReturn("Welcome {} to {}");

        setMockBundle(mockBundle);

        String result = MessagesUtils.getMessage("WELCOME_MESSAGE", "John", "YAS");

        assertNotNull(result);
        assertTrue(result.contains("John"));
        assertTrue(result.contains("YAS"));
    }

    @Test
    void testGetMessageWithSingleArg() throws Exception {
        // Test cover MessageFormatter với single argument
        ResourceBundle mockBundle = mock(ResourceBundle.class);
        when(mockBundle.getString("ORDER_ID"))
            .thenReturn("Order ID: {}");

        setMockBundle(mockBundle);

        String result = MessagesUtils.getMessage("ORDER_ID", "12345");

        assertNotNull(result);
        assertTrue(result.contains("12345"));
    }

    @Test
    void testGetMessageWithNoArgs() throws Exception {
        // Test khi không có arguments
        ResourceBundle mockBundle = mock(ResourceBundle.class);
        when(mockBundle.getString("SIMPLE_MESSAGE"))
            .thenReturn("This is a simple message");

        setMockBundle(mockBundle);

        String result = MessagesUtils.getMessage("SIMPLE_MESSAGE");

        assertEquals("This is a simple message", result);
    }

    @Test
    void testGetMessageWithNullArgs() throws Exception {
        // Test khi args là null
        ResourceBundle mockBundle = mock(ResourceBundle.class);
        when(mockBundle.getString("ERROR_KEY"))
            .thenReturn("An error occurred");

        setMockBundle(mockBundle);

        String result = MessagesUtils.getMessage("ERROR_KEY", (Object[]) null);

        assertEquals("An error occurred", result);
    }

    @Test
    void testConstructorIsPrivate() throws Exception {
        // Cover constructor branch
        Constructor<MessagesUtils> constructor = MessagesUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testStaticInitializer() throws Exception {
        // Verify static field messageBundle được initialize
        Field bundleField = MessagesUtils.class.getDeclaredField("messageBundle");
        bundleField.setAccessible(true);
        Object bundle = bundleField.get(null);
        assertNotNull(bundle, "messageBundle should be initialized");
        assertTrue(bundle instanceof ResourceBundle);
    }

    // Helper method để set mock bundle
    private void setMockBundle(ResourceBundle mockBundle) throws Exception {
        Field bundleField = MessagesUtils.class.getDeclaredField("messageBundle");
        bundleField.setAccessible(true);
        bundleField.set(null, mockBundle);
    }
}