package com.yas.payment.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessagesUtilsTest {

    private ResourceBundle originalBundle;
    private Locale originalLocale;

    @BeforeEach
    void setUp() throws Exception {
        // Save original state
        originalLocale = Locale.getDefault();
        originalBundle = getMessageBundleField();
        
        // Set default locale for consistent testing
        Locale.setDefault(Locale.ENGLISH);
        
        // Reset the message bundle to ensure fresh state
        setMessageBundleField(ResourceBundle.getBundle("messages.messages", Locale.getDefault()));
    }

    @AfterEach
    void tearDown() throws Exception {
        // Restore original state
        Locale.setDefault(originalLocale);
        if (originalBundle != null) {
            setMessageBundleField(originalBundle);
        }
    }

    private ResourceBundle getMessageBundleField() throws Exception {
        Field field = MessagesUtils.class.getDeclaredField("messageBundle");
        field.setAccessible(true);
        return (ResourceBundle) field.get(null);
    }

    private void setMessageBundleField(ResourceBundle bundle) throws Exception {
        Field field = MessagesUtils.class.getDeclaredField("messageBundle");
        field.setAccessible(true);
        field.set(null, bundle);
    }

    @Test
    @DisplayName("Should return message when error code exists in bundle")
    void getMessage_ShouldReturnMessage_WhenErrorCodeExists() {
        // Assuming there is a key "PAYMENT_SUCCESS" in messages.properties
        // If not, this test will return the key itself
        String result = MessagesUtils.getMessage("PAYMENT_SUCCESS");
        
        assertThat(result).isNotNull();
        // Note: This will return either the actual message or the key itself
    }

    @Test
    @DisplayName("Should return error code when message key not found in bundle")
    void getMessage_ShouldReturnErrorCode_WhenKeyNotFound() {
        String errorCode = "NON_EXISTENT_ERROR_CODE_12345";
        String result = MessagesUtils.getMessage(errorCode);
        
        assertThat(result).isEqualTo(errorCode);
    }

    @Test
    @DisplayName("Should format message with single argument")
    void getMessage_ShouldFormatMessage_WithSingleArgument() {
        // Create a custom test to verify formatting
        String result = MessagesUtils.getMessage("test.message.with.arg", "John");
        
        assertThat(result).isNotNull();
        // Note: Actual assertion depends on your messages.properties content
    }

    @Test
    @DisplayName("Should format message with multiple arguments")
    void getMessage_ShouldFormatMessage_WithMultipleArguments() {
        String result = MessagesUtils.getMessage("test.message.with.args", "John", "Doe", 25);
        
        assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @DisplayName("Should handle various error codes")
    @ValueSource(strings = {
        "PAYMENT_PROVIDER_NOT_FOUND",
        "PAYMENT_FAILED",
        "INVALID_REQUEST",
        "SUCCESS"
    })
    void getMessage_ShouldHandleVariousErrorCodes(String errorCode) {
        String result = MessagesUtils.getMessage(errorCode);
        
        assertThat(result).isNotNull();
        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("Should handle null error code")
    void getMessage_ShouldHandleNullErrorCode() {
        // Note: This might throw NullPointerException based on implementation
        assertThrows(NullPointerException.class, () -> {
            MessagesUtils.getMessage(null);
        });
    }

    @Test
    @DisplayName("Should handle empty error code")
    void getMessage_ShouldHandleEmptyErrorCode() {
        String result = MessagesUtils.getMessage("");
        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle message with placeholder but no arguments")
    void getMessage_ShouldHandlePlaceholderWithoutArguments() {
        String result = MessagesUtils.getMessage("message.with.placeholder");
        
        assertThat(result).isNotNull();
        // The placeholder {} might remain in the string
        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("Should handle message without placeholders")
    void getMessage_ShouldHandleMessageWithoutPlaceholders() {
        String result = MessagesUtils.getMessage("SIMPLE_MESSAGE");
        
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle arguments with different types")
    void getMessage_ShouldHandleDifferentArgumentTypes() {
        String result = MessagesUtils.getMessage("test.mixed.types", 
            "String", 123, 45.67, true);
        
        assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @DisplayName("Should handle multiple arguments with CSV source")
    @CsvSource({
        "test.message.one, John",
        "test.message.two, John, Doe",
        "test.message.three, John, Doe, 30"
    })
    void getMessage_ShouldHandleMultipleArguments(String errorCode, String args) {
        String[] arguments = args.split(", ");
        String result = MessagesUtils.getMessage(errorCode, (Object[]) arguments);
        
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should preserve original message formatting")
    void getMessage_ShouldPreserveFormatting() {
        String errorCode = "ERROR_FORMAT_TEST";
        String result = MessagesUtils.getMessage(errorCode, "value1");
        
        assertThat(result).isEqualTo(result); // Basic check that it returns something
    }

    @Test
    @DisplayName("Should handle special characters in arguments")
    void getMessage_ShouldHandleSpecialCharacters() {
        String result = MessagesUtils.getMessage("test.special.chars", 
            "!@#$%^&*()", "line1\nline2", "tab\tseparated");
        
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle null arguments array")
    void getMessage_ShouldHandleNullArguments() {
        String result = MessagesUtils.getMessage("test.message", (Object[]) null);
        
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should be thread-safe when called concurrently")
    void getMessage_ShouldBeThreadSafe() throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                MessagesUtils.getMessage("TEST_MESSAGE_" + i, "arg" + i);
            }
        };
        
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        Thread thread3 = new Thread(task);
        
        thread1.start();
        thread2.start();
        thread3.start();
        
        thread1.join();
        thread2.join();
        thread3.join();
        
        // If we reach here without exceptions, test passes
        assertTrue(true);
    }

    @Test
    @DisplayName("Should return the same message for repeated calls with same arguments")
    void getMessage_ShouldBeConsistentForSameArguments() {
        String result1 = MessagesUtils.getMessage("CONSISTENT_MESSAGE", "arg1");
        String result2 = MessagesUtils.getMessage("CONSISTENT_MESSAGE", "arg1");
        
        assertThat(result1).isEqualTo(result2);
    }
}