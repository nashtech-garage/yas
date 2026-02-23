package com.yas.search.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProductEsDetailVmTest {

    @Test
    void testRecord_whenAllFieldsProvided_thenAllFieldsSet() {
        // Given
        Long id = 1L;
        String name = "Wireless Speaker";
        String slug = "wireless-speaker";
        Double price = 99.99;
        boolean isPublished = true;
        boolean isVisibleIndividually = true;
        boolean isAllowedToOrder = true;
        boolean isFeatured = false;
        Long thumbnailMediaId = 100L;
        String brand = "AudioTech";
        List<String> categories = List.of("Electronics", "Audio");
        List<String> attributes = List.of("Bluetooth", "Wireless");

        // When
        ProductEsDetailVm vm = new ProductEsDetailVm(
                id, name, slug, price, isPublished, isVisibleIndividually,
                isAllowedToOrder, isFeatured, thumbnailMediaId, brand, categories, attributes);

        // Then
        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Wireless Speaker", vm.name());
        assertEquals("wireless-speaker", vm.slug());
        assertEquals(99.99, vm.price());
        assertTrue(vm.isPublished());
        assertTrue(vm.isVisibleIndividually());
        assertTrue(vm.isAllowedToOrder());
        assertFalse(vm.isFeatured());
        assertEquals(100L, vm.thumbnailMediaId());
        assertEquals("AudioTech", vm.brand());
        assertEquals(2, vm.categories().size());
        assertEquals(2, vm.attributes().size());
    }

    @Test
    void testRecord_whenBooleanFieldsFalse_thenFieldsAreFalse() {
        // Given
        ProductEsDetailVm vm = new ProductEsDetailVm(
                2L, "Hidden Product", "hidden-product", 149.99,
                false, false, false, false, 200L, "TestBrand",
                List.of(), List.of());

        // Then
        assertNotNull(vm);
        assertFalse(vm.isPublished());
        assertFalse(vm.isVisibleIndividually());
        assertFalse(vm.isAllowedToOrder());
        assertFalse(vm.isFeatured());
    }

    @Test
    void testRecord_whenEmptyCollections_thenEmptyCollectionsSet() {
        // Given
        List<String> emptyCategories = List.of();
        List<String> emptyAttributes = List.of();

        // When
        ProductEsDetailVm vm = new ProductEsDetailVm(
                3L, "Simple Product", "simple-product", 49.99,
                true, true, true, true, 300L, "SimpleBrand",
                emptyCategories, emptyAttributes);

        // Then
        assertNotNull(vm);
        assertTrue(vm.categories().isEmpty());
        assertTrue(vm.attributes().isEmpty());
    }

    @Test
    void testRecord_whenMultipleCategoriesAndAttributes_thenAllPresent() {
        // Given
        List<String> categories = List.of("Cat1", "Cat2", "Cat3");
        List<String> attributes = List.of("Attr1", "Attr2", "Attr3", "Attr4");

        // When
        ProductEsDetailVm vm = new ProductEsDetailVm(
                4L, "Complex Product", "complex-product", 199.99,
                true, true, true, true, 400L, "ComplexBrand",
                categories, attributes);

        // Then
        assertNotNull(vm);
        assertEquals(3, vm.categories().size());
        assertEquals(4, vm.attributes().size());
    }

    @Test
    void testRecord_whenNullCollections_thenNullCollectionsSet() {
        // Given
        ProductEsDetailVm vm = new ProductEsDetailVm(
                5L, "Null Collections Product", "null-collections", 299.99,
                true, true, true, false, 500L, "NullBrand",
                null, null);

        // Then
        assertNotNull(vm);
        assertEquals(null, vm.categories());
        assertEquals(null, vm.attributes());
    }
}
