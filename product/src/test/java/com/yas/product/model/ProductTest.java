package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.product.model.enumeration.DimensionUnit;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void testProductProperties() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product Name");
        product.setShortDescription("Short");
        product.setDescription("Desc");
        product.setSpecification("Spec");
        product.setSku("sku");
        product.setGtin("gtin");
        product.setSlug("slug");
        product.setPrice(10.0);
        product.setHasOptions(true);
        product.setAllowedToOrder(true);
        product.setPublished(true);
        product.setFeatured(true);
        product.setVisibleIndividually(true);
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(100L);
        product.setTaxClassId(1L);
        product.setMetaTitle("metaTitle");
        product.setMetaKeyword("metaKeyword");
        product.setMetaDescription("metaDescription");
        product.setThumbnailMediaId(1L);
        product.setWeight(1.0);
        product.setDimensionUnit(DimensionUnit.CM);
        product.setLength(1.0);
        product.setWidth(1.0);
        product.setHeight(1.0);
        
        Brand brand = new Brand();
        brand.setId(1L);
        product.setBrand(brand);
        
        product.setProductCategories(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        product.setProductImages(new ArrayList<>());
        
        Product parent = new Product();
        parent.setId(2L);
        product.setParent(parent);
        product.setProducts(new ArrayList<>());
        product.setTaxIncluded(true);
        
        product.setRelatedProducts(new ArrayList<>());

        assertEquals(1L, product.getId());
        assertEquals("Product Name", product.getName());
        assertEquals("Short", product.getShortDescription());
        assertEquals("Desc", product.getDescription());
        assertEquals("Spec", product.getSpecification());
        assertEquals("sku", product.getSku());
        assertEquals("gtin", product.getGtin());
        assertEquals("slug", product.getSlug());
        assertEquals(10.0, product.getPrice());
        assertTrue(product.isHasOptions());
        assertTrue(product.isAllowedToOrder());
        assertTrue(product.isPublished());
        assertTrue(product.isFeatured());
        assertTrue(product.isVisibleIndividually());
        assertTrue(product.isStockTrackingEnabled());
        assertEquals(100L, product.getStockQuantity());
        assertEquals(1L, product.getTaxClassId());
        assertEquals("metaTitle", product.getMetaTitle());
        assertEquals("metaKeyword", product.getMetaKeyword());
        assertEquals("metaDescription", product.getMetaDescription());
        assertEquals(1L, product.getThumbnailMediaId());
        assertEquals(1.0, product.getWeight());
        assertEquals(DimensionUnit.CM, product.getDimensionUnit());
        assertEquals(1.0, product.getLength());
        assertEquals(1.0, product.getWidth());
        assertEquals(1.0, product.getHeight());
        assertNotNull(product.getBrand());
        assertNotNull(product.getProductCategories());
        assertNotNull(product.getAttributeValues());
        assertNotNull(product.getProductImages());
        assertNotNull(product.getParent());
        assertNotNull(product.getProducts());
        assertTrue(product.isTaxIncluded());
        assertNotNull(product.getRelatedProducts());
        
        Product p2 = new Product();
        p2.setId(1L);
        assertTrue(product.equals(p2));
        assertFalse(product.equals(new Object()));
        assertNotNull(product.hashCode());
        
        Product p3 = Product.builder().id(3L).name("Builder").build();
        assertEquals(3L, p3.getId());
    }
}
