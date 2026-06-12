package com.yas.recommendation.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void testRelatedProductVm() {
        RelatedProductVm vm = new RelatedProductVm();
        vm.setProductId(1L);
        vm.setName("Product Name");
        vm.setPrice(BigDecimal.TEN);
        vm.setBrand("Brand");
        vm.setTitle("Title");
        vm.setDescription("Description");
        vm.setMetaDescription("Meta Description");
        vm.setSpecification("Specification");
        vm.setSlug("slug");

        ImageVm thumbnail = new ImageVm(1L, "url");
        vm.setThumbnail(thumbnail);
        vm.setProductImages(List.of(thumbnail));

        assertEquals(1L, vm.getProductId());
        assertEquals("Product Name", vm.getName());
        assertEquals(BigDecimal.TEN, vm.getPrice());
        assertEquals("Brand", vm.getBrand());
        assertEquals("Title", vm.getTitle());
        assertEquals("Description", vm.getDescription());
        assertEquals("Meta Description", vm.getMetaDescription());
        assertEquals("Specification", vm.getSpecification());
        assertEquals("slug", vm.getSlug());
        assertEquals(thumbnail, vm.getThumbnail());
        assertEquals(1, vm.getProductImages().size());
    }

    @Test
    void testProductDetailVm() {
        ProductDetailVm vm = new ProductDetailVm(
            1L, "Name", "Short", "Desc", "Spec", "SKU", "GTIN", "slug",
            true, true, true, true, true, 10.0, 1L, List.of(),
            "MetaTitle", "MetaKeyword", "MetaDesc", 1L, "BrandName",
            List.of(), List.of(), new ImageVm(1L, "url"), List.of()
        );

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Name", vm.name());
        assertEquals("Short", vm.shortDescription());
        assertEquals("Desc", vm.description());
        assertEquals("Spec", vm.specification());
        assertEquals("SKU", vm.sku());
        assertEquals("GTIN", vm.gtin());
        assertEquals("slug", vm.slug());
    }

    @Test
    void testImageVm() {
        ImageVm vm = new ImageVm(1L, "url");
        assertEquals(1L, vm.id());
        assertEquals("url", vm.url());
    }
}
