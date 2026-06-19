package com.yas.product.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void testConstantsInstantiation() {
        Constants constants = new Constants();
        assertNotNull(constants);
        
        Constants.ErrorCode errorCode = constants.new ErrorCode();
        assertNotNull(errorCode);
    }

    @Test
    void testProductConverterToSlug() {
        assertEquals("test-product", ProductConverter.toSlug("Test Product"));
        assertEquals("test-product-", ProductConverter.toSlug("-Test Product-"));
        assertEquals("test-product", ProductConverter.toSlug("Test---Product"));
    }
    
    @Test
    void testProductConverterInstantiation() {
        ProductConverter converter = new ProductConverter();
        assertNotNull(converter);
    }

    @Test
    void testMessagesUtilsInstantiation() {
        MessagesUtils utils = new MessagesUtils();
        assertNotNull(utils);
    }

    @Test
    void testMessagesUtilsGetMessage() {
        String message = MessagesUtils.getMessage("PRODUCT_NOT_FOUND");
        assertNotNull(message);
        
        String missingMessage = MessagesUtils.getMessage("SOME_RANDOM_MISSING_CODE");
        assertEquals("SOME_RANDOM_MISSING_CODE", missingMessage);
    }
}
