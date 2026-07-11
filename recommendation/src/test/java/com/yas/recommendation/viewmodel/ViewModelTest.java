package com.yas.recommendation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void productDetailRecord_shouldExposeAllConstructorValues() {
        CategoryVm category = new CategoryVm(1L, "Phones", "Desc", "phones", "kw", "meta", (short) 1, true);
        ProductAttributeValueVm attribute = new ProductAttributeValueVm(2L, "Color", "Black");
        ProductVariationVm variation = new ProductVariationVm(3L, "Black", "black", "SKU", "GTIN", 99D,
                Map.of(4L, "128GB"));
        ImageVm thumbnail = new ImageVm(5L, "/thumb.png");
        ImageVm image = new ImageVm(6L, "/image.png");

        ProductDetailVm detail = new ProductDetailVm(
                7L, "Phone", "Short", "Description", "Spec", "SKU-7", "GTIN-7", "phone",
                true, true, false, true, true, 99.99D, 8L, List.of(category),
                "Title", "Keyword", "Meta", 9L, "Brand", List.of(attribute),
                List.of(variation), thumbnail, List.of(image)
        );

        assertThat(detail.id()).isEqualTo(7L);
        assertThat(detail.categories()).containsExactly(category);
        assertThat(detail.attributeValues()).containsExactly(attribute);
        assertThat(detail.variations()).containsExactly(variation);
        assertThat(detail.thumbnail()).isEqualTo(thumbnail);
        assertThat(detail.productImages()).containsExactly(image);
        assertThat(detail.brandName()).isEqualTo("Brand");
    }

    @Test
    void relatedProductBean_shouldExposeAssignedValues() {
        ImageVm thumbnail = new ImageVm(10L, "/thumb.png");
        ImageVm image = new ImageVm(11L, "/image.png");
        RelatedProductVm related = new RelatedProductVm();

        related.setProductId(12L);
        related.setName("Related");
        related.setPrice(BigDecimal.TEN);
        related.setBrand("Brand");
        related.setTitle("Title");
        related.setDescription("Description");
        related.setMetaDescription("Meta");
        related.setSpecification("Spec");
        related.setThumbnail(thumbnail);
        related.setProductImages(List.of(image));
        related.setSlug("related");

        assertThat(related.getProductId()).isEqualTo(12L);
        assertThat(related.getName()).isEqualTo("Related");
        assertThat(related.getPrice()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(related.getBrand()).isEqualTo("Brand");
        assertThat(related.getTitle()).isEqualTo("Title");
        assertThat(related.getDescription()).isEqualTo("Description");
        assertThat(related.getMetaDescription()).isEqualTo("Meta");
        assertThat(related.getSpecification()).isEqualTo("Spec");
        assertThat(related.getThumbnail()).isEqualTo(thumbnail);
        assertThat(related.getProductImages()).containsExactly(image);
        assertThat(related.getSlug()).isEqualTo("related");
    }
}
