package com.yas.recommendation.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

class BaseDocumentTest {

    @Test
    void toDocument_shouldUseProvidedIdGeneratorAndKeepContentAndMetadata() {
        ProductDocument productDocument = new ProductDocument();
        productDocument.setContent("content");
        productDocument.setMetadata(Map.of("id", 1L, "type", "PRODUCT"));

        Document document = productDocument.toDocument(contents -> "fixed-id");

        assertThat(document.getId()).isEqualTo("fixed-id");
        assertThat(document.getContent()).isEqualTo("content");
        assertThat(document.getMetadata()).containsEntry("id", 1L).containsEntry("type", "PRODUCT");
        assertThat(document.getContentFormatter()).isSameAs(ProductDocument.DEFAULT_CONTENT_FORMATTER);
    }

    @Test
    void toDocument_whenContentIsNull_shouldFailFast() {
        ProductDocument productDocument = new ProductDocument();
        productDocument.setMetadata(Map.of("id", 1L));

        assertThatThrownBy(() -> productDocument.toDocument(contents -> "id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content cannot be null");
    }
}
