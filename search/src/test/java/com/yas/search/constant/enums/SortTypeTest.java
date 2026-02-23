package com.yas.search.constant.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SortTypeTest {

    @Test
    void testEnumValues_whenCallingValues_thenReturnAllEnumConstants() {
        // When
        SortType[] values = SortType.values();

        // Then
        assertNotNull(values);
        assertEquals(3, values.length);
    }

    @Test
    void testEnumValueOf_whenValidName_thenReturnEnumConstant() {
        // When
        SortType sortType = SortType.valueOf("DEFAULT");

        // Then
        assertNotNull(sortType);
        assertEquals(SortType.DEFAULT, sortType);
    }

    @Test
    void testEnumValueOf_whenPriceAsc_thenReturnPriceAscEnum() {
        // When
        SortType sortType = SortType.valueOf("PRICE_ASC");

        // Then
        assertNotNull(sortType);
        assertEquals(SortType.PRICE_ASC, sortType);
    }

    @Test
    void testEnumValueOf_whenPriceDesc_thenReturnPriceDescEnum() {
        // When
        SortType sortType = SortType.valueOf("PRICE_DESC");

        // Then
        assertNotNull(sortType);
        assertEquals(SortType.PRICE_DESC, sortType);
    }

    @Test
    void testEnumComparison_whenComparingEnums_thenEqualityWorks() {
        // Given
        SortType sort1 = SortType.DEFAULT;
        SortType sort2 = SortType.DEFAULT;
        SortType sort3 = SortType.PRICE_ASC;

        // Then
        assertEquals(sort1, sort2);
        assertNotNull(sort3);
    }
}
