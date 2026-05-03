package com.yas.product.utils;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConstantsTest {

    @Test
    void testConstantsInstantiation() throws NoSuchMethodException {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        }
    }

    @Test
    void testErrorCodeInstantiation() throws NoSuchMethodException {
        Constructor<Constants.ErrorCode> constructor = Constants.ErrorCode.class.getDeclaredConstructor(Constants.class);
        constructor.setAccessible(true);
        try {
            constructor.newInstance(new Constants());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        }
    }
    
    @Test
    void testConstantValues() {
         assertEquals("PRODUCT_NOT_FOUND", Constants.ErrorCode.PRODUCT_NOT_FOUND);
    }
}