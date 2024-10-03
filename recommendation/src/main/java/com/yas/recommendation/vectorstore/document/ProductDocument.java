package com.yas.recommendation.vectorstore.document;

import java.util.Collection;
import java.util.Map;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.model.Media;

/**
 * Represents a document that contains product-related information. The content of this document
 * is formatted using a custom content formatter.
 */
public class ProductDocument extends Document {

    public static final ContentFormatter CUSTOM_CONTENT_FORMATTER = DefaultContentFormatter.builder()
        .from(DefaultContentFormatter.defaultConfig())
        .withTextTemplate("{content}")
        .build();

    public ProductDocument(String content) {
        super(content);
        setContentFormatter(CUSTOM_CONTENT_FORMATTER);
    }

    public ProductDocument(String content, Map<String, Object> metadata) {
        super(content, metadata);
        setContentFormatter(CUSTOM_CONTENT_FORMATTER);
    }

    public ProductDocument(String content, Collection<Media> media,
                           Map<String, Object> metadata) {
        super(content, media, metadata);
        setContentFormatter(CUSTOM_CONTENT_FORMATTER);
    }

    public ProductDocument(String content, Map<String, Object> metadata, IdGenerator idGenerator) {
        super(content, metadata, idGenerator);
        setContentFormatter(CUSTOM_CONTENT_FORMATTER);
    }

    public ProductDocument(String id, String content, Map<String, Object> metadata) {
        super(id, content, metadata);
        setContentFormatter(CUSTOM_CONTENT_FORMATTER);
    }

    public ProductDocument(String id, String content, Collection<Media> media, Map<String, Object> metadata) {
        super(id, content, media, metadata);
        setContentFormatter(CUSTOM_CONTENT_FORMATTER);
    }
}
