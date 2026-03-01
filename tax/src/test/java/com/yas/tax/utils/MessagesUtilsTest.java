package com.yas.tax.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    private ResourceBundle originalBundle;

    @BeforeEach
    void captureOriginal() throws Exception {
        Field bundleField = MessagesUtils.class.getDeclaredField("messageBundle");
        bundleField.setAccessible(true);
        originalBundle = (ResourceBundle) bundleField.get(null);
    }

    @AfterEach
    void restoreOriginal() throws Exception {
        Field bundleField = MessagesUtils.class.getDeclaredField("messageBundle");
        bundleField.setAccessible(true);
        bundleField.set(null, originalBundle);
    }

    @Test
    void getMessage_whenKeyMissing_shouldFallbackToCode() throws Exception {
        ResourceBundle emptyBundle = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[0][0];
            }
        };
        setBundle(emptyBundle);

        String result = MessagesUtils.getMessage("UNKNOWN_CODE");

        assertThat(result).isEqualTo("UNKNOWN_CODE");
    }

    @Test
    void getMessage_shouldFormatPlaceholders() throws Exception {
        ResourceBundle bundle = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] { {"GREETING", "Hello {}"} };
            }
        };
        setBundle(bundle);

        String result = MessagesUtils.getMessage("GREETING", "World");

        assertThat(result).isEqualTo("Hello World");
    }

    private void setBundle(ResourceBundle bundle) throws Exception {
        Field bundleField = MessagesUtils.class.getDeclaredField("messageBundle");
        bundleField.setAccessible(true);
        bundleField.set(null, bundle);
    }
}
