package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.utils.ProductConverter;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ModelAndUtilsTest {

    // === ProductRelated equals/hashCode ===
    @Test
    void productRelated_equalsSameInstance() {
        ProductRelated pr = ProductRelated.builder().id(1L).build();
        assertEquals(pr, pr);
    }

    @Test
    void productRelated_equalsById() {
        ProductRelated pr1 = ProductRelated.builder().id(1L).build();
        ProductRelated pr2 = ProductRelated.builder().id(1L).build();
        assertEquals(pr1, pr2);
    }

    @Test
    void productRelated_notEqualsDifferentId() {
        ProductRelated pr1 = ProductRelated.builder().id(1L).build();
        ProductRelated pr2 = ProductRelated.builder().id(2L).build();
        assertNotEquals(pr1, pr2);
    }

    @Test
    void productRelated_notEqualsNull() {
        ProductRelated pr = ProductRelated.builder().id(1L).build();
        assertNotEquals(pr, null);
    }

    @Test
    void productRelated_notEqualsOtherType() {
        ProductRelated pr = ProductRelated.builder().id(1L).build();
        assertNotEquals(pr, "string");
    }

    @Test
    void productRelated_nullIdNotEquals() {
        ProductRelated pr1 = ProductRelated.builder().build();
        ProductRelated pr2 = ProductRelated.builder().id(1L).build();
        assertNotEquals(pr1, pr2);
    }

    @Test
    void productRelated_hashCode() {
        ProductRelated pr1 = ProductRelated.builder().id(1L).build();
        ProductRelated pr2 = ProductRelated.builder().id(2L).build();
        assertEquals(pr1.hashCode(), pr2.hashCode());
    }

    // === Product equals/hashCode ===
    @Test
    void product_equalsSameInstance() {
        Product p = Product.builder().id(1L).build();
        assertEquals(p, p);
    }

    @Test
    void product_equalsById() {
        Product p1 = Product.builder().id(1L).build();
        Product p2 = Product.builder().id(1L).build();
        assertEquals(p1, p2);
    }

    @Test
    void product_notEqualsNull() {
        Product p = Product.builder().id(1L).build();
        assertNotEquals(p, null);
    }

    @Test
    void product_notEqualsOtherType() {
        Product p = Product.builder().id(1L).build();
        assertNotEquals(p, "other");
    }

    @Test
    void product_nullIdNotEquals() {
        Product p1 = Product.builder().build();
        Product p2 = Product.builder().id(1L).build();
        assertNotEquals(p1, p2);
    }

    // === Brand equals/hashCode ===
    @Test
    void brand_equalsById() {
        Brand b1 = new Brand();
        b1.setId(1L);
        Brand b2 = new Brand();
        b2.setId(1L);
        assertEquals(b1, b2);
    }

    @Test
    void brand_notEqualsNull() {
        Brand b = new Brand();
        b.setId(1L);
        assertNotEquals(b, null);
    }

    @Test
    void brand_notEqualsOtherType() {
        Brand b = new Brand();
        b.setId(1L);
        assertNotEquals(b, "string");
    }

    @Test
    void brand_equalsSelf() {
        Brand b = new Brand();
        b.setId(1L);
        assertEquals(b, b);
    }

    @Test
    void brand_nullIdNotEquals() {
        Brand b1 = new Brand();
        Brand b2 = new Brand();
        b2.setId(1L);
        assertNotEquals(b1, b2);
    }

    // === Category equals/hashCode ===
    @Test
    void category_equalsById() {
        Category c1 = new Category();
        c1.setId(1L);
        Category c2 = new Category();
        c2.setId(1L);
        assertEquals(c1, c2);
    }

    @Test
    void category_notEqualsNull() {
        Category c = new Category();
        c.setId(1L);
        assertNotEquals(c, null);
    }

    @Test
    void category_notEqualsOtherType() {
        Category c = new Category();
        c.setId(1L);
        assertNotEquals(c, "other");
    }

    @Test
    void category_nullIdNotEquals() {
        Category c1 = new Category();
        Category c2 = new Category();
        c2.setId(1L);
        assertNotEquals(c1, c2);
    }

    // === ProductOption equals/hashCode ===
    @Test
    void productOption_equalsById() {
        ProductOption po1 = new ProductOption();
        po1.setId(1L);
        ProductOption po2 = new ProductOption();
        po2.setId(1L);
        assertEquals(po1, po2);
    }

    @Test
    void productOption_notEqualsNull() {
        ProductOption po = new ProductOption();
        po.setId(1L);
        assertNotEquals(po, null);
    }

    @Test
    void productOption_nullIdNotEquals() {
        ProductOption po1 = new ProductOption();
        ProductOption po2 = new ProductOption();
        po2.setId(1L);
        assertNotEquals(po1, po2);
    }

    // === ProductOptionValue equals/hashCode ===
    @Test
    void productOptionValue_equalsById() {
        ProductOptionValue pov1 = new ProductOptionValue();
        pov1.setId(1L);
        ProductOptionValue pov2 = new ProductOptionValue();
        pov2.setId(1L);
        assertEquals(pov1, pov2);
    }

    @Test
    void productOptionValue_notEqualsNull() {
        ProductOptionValue pov = new ProductOptionValue();
        pov.setId(1L);
        assertNotEquals(pov, null);
    }

    @Test
    void productOptionValue_nullIdNotEquals() {
        ProductOptionValue pov1 = new ProductOptionValue();
        ProductOptionValue pov2 = new ProductOptionValue();
        pov2.setId(1L);
        assertNotEquals(pov1, pov2);
    }

    // === ProductOptionCombination equals/hashCode ===
    @Test
    void productOptionCombination_equalsById() {
        ProductOptionCombination poc1 = ProductOptionCombination.builder().id(1L).build();
        ProductOptionCombination poc2 = ProductOptionCombination.builder().id(1L).build();
        assertEquals(poc1, poc2);
    }

    @Test
    void productOptionCombination_notEqualsNull() {
        ProductOptionCombination poc = ProductOptionCombination.builder().id(1L).build();
        assertNotEquals(poc, null);
    }

    @Test
    void productOptionCombination_nullIdNotEquals() {
        ProductOptionCombination poc1 = ProductOptionCombination.builder().build();
        ProductOptionCombination poc2 = ProductOptionCombination.builder().id(1L).build();
        assertNotEquals(poc1, poc2);
    }

    // === ProductAttribute equals/hashCode ===
    @Test
    void productAttribute_equalsById() {
        ProductAttribute a1 = new ProductAttribute();
        a1.setId(1L);
        ProductAttribute a2 = new ProductAttribute();
        a2.setId(1L);
        assertEquals(a1, a2);
    }

    @Test
    void productAttribute_notEqualsNull() {
        ProductAttribute a = new ProductAttribute();
        a.setId(1L);
        assertNotEquals(a, null);
    }

    @Test
    void productAttribute_nullIdNotEquals() {
        ProductAttribute a1 = new ProductAttribute();
        ProductAttribute a2 = new ProductAttribute();
        a2.setId(1L);
        assertNotEquals(a1, a2);
    }

    // === ProductTemplate equals/hashCode ===
    @Test
    void productTemplate_equalsById() {
        ProductTemplate t1 = new ProductTemplate();
        t1.setId(1L);
        ProductTemplate t2 = new ProductTemplate();
        t2.setId(1L);
        assertEquals(t1, t2);
    }

    @Test
    void productTemplate_notEqualsNull() {
        ProductTemplate t = new ProductTemplate();
        t.setId(1L);
        assertNotEquals(t, null);
    }

    @Test
    void productTemplate_nullIdNotEquals() {
        ProductTemplate t1 = new ProductTemplate();
        ProductTemplate t2 = new ProductTemplate();
        t2.setId(1L);
        assertNotEquals(t1, t2);
    }

    // === ProductAttributeGroup equals/hashCode ===
    @Test
    void productAttributeGroup_equalsById() {
        ProductAttributeGroup g1 = new ProductAttributeGroup();
        g1.setId(1L);
        ProductAttributeGroup g2 = new ProductAttributeGroup();
        g2.setId(1L);
        assertEquals(g1, g2);
    }

    @Test
    void productAttributeGroup_notEqualsNull() {
        ProductAttributeGroup g = new ProductAttributeGroup();
        g.setId(1L);
        assertNotEquals(g, null);
    }

    @Test
    void productAttributeGroup_nullIdNotEquals() {
        ProductAttributeGroup g1 = new ProductAttributeGroup();
        ProductAttributeGroup g2 = new ProductAttributeGroup();
        g2.setId(1L);
        assertNotEquals(g1, g2);
    }

    // === ProductConverter ===
    @Test
    void toSlug_basic() {
        assertEquals("hello-world", ProductConverter.toSlug("Hello World"));
    }

    @Test
    void toSlug_specialChars() {
        assertEquals("hello-world-", ProductConverter.toSlug("Hello@World!"));
    }

    @Test
    void toSlug_leadingDash() {
        assertEquals("test", ProductConverter.toSlug("@test"));
    }

    @Test
    void toSlug_multipleDashes() {
        assertEquals("a-b", ProductConverter.toSlug("a---b"));
    }

    @Test
    void toSlug_trimAndLowerCase() {
        assertEquals("test", ProductConverter.toSlug("  TEST  "));
    }

    // === ErrorVm ===
    @Test
    void errorVm_withFieldErrors() {
        ErrorVm error = new ErrorVm("400", "Bad Request", "detail", List.of("field1"));
        assertEquals("400", error.statusCode());
        assertEquals(1, error.fieldErrors().size());
    }

    @Test
    void errorVm_withoutFieldErrors() {
        ErrorVm error = new ErrorVm("500", "Server Error", "detail");
        assertEquals("500", error.statusCode());
        assertTrue(error.fieldErrors().isEmpty());
    }

    // === ProductDetailInfoVm ===
    @Test
    void productDetailInfoVm_constructorNullHandling() {
        ProductDetailInfoVm vm = new ProductDetailInfoVm(
                1L, "name", "short", "desc", "spec", "sku", "gtin", "slug",
                true, true, false, true, false, 100.0, 1L,
                null, "mt", "mk", "md", 1L, "brand",
                null, null, null, null);
        assertNotNull(vm.getCategories());
        assertTrue(vm.getCategories().isEmpty());
        assertNotNull(vm.getAttributeValues());
        assertNotNull(vm.getVariations());
    }

    @Test
    void productDetailInfoVm_constructorWithValues() {
        ProductDetailInfoVm vm = new ProductDetailInfoVm(
                1L, "name", "short", "desc", "spec", "sku", "gtin", "slug",
                true, true, false, true, false, 100.0, 1L,
                List.of(), "mt", "mk", "md", 1L, "brand",
                List.of(), List.of(), null, List.of());
        assertEquals("name", vm.getName());
        assertEquals(100.0, vm.getPrice());
    }
}
