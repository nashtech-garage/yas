package com.yas.search.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.search.constant.enums.SortType;
import org.junit.jupiter.api.Test;

class ProductCriteriaDtoTest {

    @Test
    void testRecord_whenAllFieldsProvided_thenAllFieldsSet() {
        // Given
        String keyword = "wireless";
        Integer page = 0;
        Integer size = 10;
        String brand = "AudioTech";
        String category = "Electronics";
        String attribute = "Bluetooth";
        Double minPrice = 50.0;
        Double maxPrice = 200.0;
        SortType sortType = SortType.PRICE_ASC;

        // When
        ProductCriteriaDto dto = new ProductCriteriaDto(
                keyword, page, size, brand, category, attribute,
                minPrice, maxPrice, sortType);

        // Then
        assertNotNull(dto);
        assertEquals("wireless", dto.keyword());
        assertEquals(0, dto.page());
        assertEquals(10, dto.size());
        assertEquals("AudioTech", dto.brand());
        assertEquals("Electronics", dto.category());
        assertEquals("Bluetooth", dto.attribute());
        assertEquals(50.0, dto.minPrice());
        assertEquals(200.0, dto.maxPrice());
        assertEquals(SortType.PRICE_ASC, dto.sortType());
    }

    @Test
    void testRecord_whenNullOptionalFields_thenNullFieldsSet() {
        // Given
        String keyword = "test";
        Integer page = 1;
        Integer size = 20;
        SortType sortType = SortType.DEFAULT;

        // When
        ProductCriteriaDto dto = new ProductCriteriaDto(
                keyword, page, size, null, null, null, null, null, sortType);

        // Then
        assertNotNull(dto);
        assertEquals("test", dto.keyword());
        assertEquals(1, dto.page());
        assertEquals(20, dto.size());
        assertNull(dto.brand());
        assertNull(dto.category());
        assertNull(dto.attribute());
        assertNull(dto.minPrice());
        assertNull(dto.maxPrice());
        assertEquals(SortType.DEFAULT, dto.sortType());
    }

    @Test
    void testRecord_whenSortTypePriceDesc_thenSortTypeSet() {
        // Given
        ProductCriteriaDto dto = new ProductCriteriaDto(
                "product", 0, 10, "Brand", "Category", "Attr",
                10.0, 100.0, SortType.PRICE_DESC);

        // Then
        assertNotNull(dto);
        assertEquals(SortType.PRICE_DESC, dto.sortType());
    }

    @Test
    void testRecord_whenMinPriceOnly_thenOnlyMinPriceSet() {
        // Given
        ProductCriteriaDto dto = new ProductCriteriaDto(
                "budget", 0, 10, null, null, null,
                25.0, null, SortType.DEFAULT);

        // Then
        assertNotNull(dto);
        assertEquals(25.0, dto.minPrice());
        assertNull(dto.maxPrice());
    }

    @Test
    void testRecord_whenMaxPriceOnly_thenOnlyMaxPriceSet() {
        // Given
        ProductCriteriaDto dto = new ProductCriteriaDto(
                "expensive", 0, 10, null, null, null,
                null, 500.0, SortType.DEFAULT);

        // Then
        assertNotNull(dto);
        assertNull(dto.minPrice());
        assertEquals(500.0, dto.maxPrice());
    }

    @Test
    void testRecord_whenMultipleCategoriesInString_thenCategoryStringSet() {
        // Given
        String categories = "Electronics,Audio,Gadgets";

        // When
        ProductCriteriaDto dto = new ProductCriteriaDto(
                "multi", 0, 10, "Brand", categories, "Attr",
                null, null, SortType.DEFAULT);

        // Then
        assertNotNull(dto);
        assertEquals("Electronics,Audio,Gadgets", dto.category());
    }

    @Test
    void testRecord_whenEmptyStringKeyword_thenEmptyStringSet() {
        // Given
        ProductCriteriaDto dto = new ProductCriteriaDto(
                "", 0, 10, null, null, null,
                null, null, SortType.DEFAULT);

        // Then
        assertNotNull(dto);
        assertEquals("", dto.keyword());
    }

    @Test
    void testRecord_whenLargePage_thenLargePageSet() {
        // Given
        ProductCriteriaDto dto = new ProductCriteriaDto(
                "keyword", 1000, 100, null, null, null,
                null, null, SortType.DEFAULT);

        // Then
        assertNotNull(dto);
        assertEquals(1000, dto.page());
        assertEquals(100, dto.size());
    }
}
