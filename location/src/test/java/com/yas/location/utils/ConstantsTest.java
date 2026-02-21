package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void testErrorCodeConstants_AllDefined() {
        assertNotNull(Constants.ErrorCode.COUNTRY_NOT_FOUND);
        assertNotNull(Constants.ErrorCode.NAME_ALREADY_EXITED);
        assertNotNull(Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND);
        assertNotNull(Constants.ErrorCode.ADDRESS_NOT_FOUND);
        assertNotNull(Constants.ErrorCode.CODE_ALREADY_EXISTED);
    }

    @Test
    void testErrorCodeConstants_CorrectValues() {
        assertEquals("COUNTRY_NOT_FOUND", Constants.ErrorCode.COUNTRY_NOT_FOUND);
        assertEquals("NAME_ALREADY_EXITED", Constants.ErrorCode.NAME_ALREADY_EXITED);
        assertEquals("STATE_OR_PROVINCE_NOT_FOUND", Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND);
        assertEquals("ADDRESS_NOT_FOUND", Constants.ErrorCode.ADDRESS_NOT_FOUND);
        assertEquals("CODE_ALREADY_EXISTED", Constants.ErrorCode.CODE_ALREADY_EXISTED);
    }

    @Test
    void testPageableConstantConstants_AllDefined() {
        assertNotNull(Constants.PageableConstant.DEFAULT_PAGE_SIZE);
        assertNotNull(Constants.PageableConstant.DEFAULT_PAGE_NUMBER);
    }

    @Test
    void testPageableConstantConstants_CorrectValues() {
        assertEquals("10", Constants.PageableConstant.DEFAULT_PAGE_SIZE);
        assertEquals("0", Constants.PageableConstant.DEFAULT_PAGE_NUMBER);
    }

    @Test
    void testApiConstantConstants_AllDefined() {
        assertNotNull(Constants.ApiConstant.STATE_OR_PROVINCES_URL);
        assertNotNull(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL);
        assertNotNull(Constants.ApiConstant.COUNTRIES_URL);
        assertNotNull(Constants.ApiConstant.COUNTRIES_STOREFRONT_URL);
    }

    @Test
    void testApiConstantConstants_CorrectValues() {
        assertEquals("/backoffice/state-or-provinces", Constants.ApiConstant.STATE_OR_PROVINCES_URL);
        assertEquals("/storefront/state-or-provinces", Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL);
        assertEquals("/backoffice/countries", Constants.ApiConstant.COUNTRIES_URL);
        assertEquals("/storefront/countries", Constants.ApiConstant.COUNTRIES_STOREFRONT_URL);
    }

    @Test
    void testApiConstantStatusCodes_AllDefined() {
        assertNotNull(Constants.ApiConstant.CODE_200);
        assertNotNull(Constants.ApiConstant.CODE_201);
        assertNotNull(Constants.ApiConstant.CODE_204);
        assertNotNull(Constants.ApiConstant.CODE_400);
        assertNotNull(Constants.ApiConstant.CODE_404);
    }

    @Test
    void testApiConstantStatusCodes_CorrectValues() {
        assertEquals("200", Constants.ApiConstant.CODE_200);
        assertEquals("201", Constants.ApiConstant.CODE_201);
        assertEquals("204", Constants.ApiConstant.CODE_204);
        assertEquals("400", Constants.ApiConstant.CODE_400);
        assertEquals("404", Constants.ApiConstant.CODE_404);
    }

    @Test
    void testApiConstantMessages_AllDefined() {
        assertNotNull(Constants.ApiConstant.OK);
        assertNotNull(Constants.ApiConstant.CREATED);
        assertNotNull(Constants.ApiConstant.NO_CONTENT);
        assertNotNull(Constants.ApiConstant.BAD_REQUEST);
        assertNotNull(Constants.ApiConstant.NOT_FOUND);
    }

    @Test
    void testApiConstantMessages_CorrectValues() {
        assertEquals("Ok", Constants.ApiConstant.OK);
        assertEquals("Created", Constants.ApiConstant.CREATED);
        assertEquals("No content", Constants.ApiConstant.NO_CONTENT);
        assertEquals("Bad request", Constants.ApiConstant.BAD_REQUEST);
        assertEquals("Not found", Constants.ApiConstant.NOT_FOUND);
    }

    @Test
    void testConstantsIsNotNull() {
        assertNotNull(new Constants());
    }

    @Test
    void testErrorCodeIsNotNull() {
        assertNotNull(Constants.ErrorCode.class);
    }

    @Test
    void testPageableConstantIsNotNull() {
        assertNotNull(Constants.PageableConstant.class);
    }

    @Test
    void testApiConstantIsNotNull() {
        assertNotNull(Constants.ApiConstant.class);
    }
}
