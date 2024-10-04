package com.yas.recommendation.vector.document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the format of a document for use in vector searches.
 * Defines the format value and a content formatter for embeddings.
 *
 * <p><strong>Embedded Content:</strong></p>
 * <p>By default, when using {@link org.springframework.ai.document.Document}, the embedding content
 * combines both content and metadata in the following format:</p>
 * <pre>
 * String.format("%s\n\n%s", "{metadata_string}", "{content}")
 * </pre>
 * <p>This annotation allows configuration of how the document's content will be embedded.
 * For example, to embed only the content, declare <code>embeddingContentFormatter = '{content}'</code>.</p>
 *
 * <p><strong>Query Format:</strong></p>
 * <p>Specifies how to format an entity as a query before performing a search.</p>
 * For example, we have Product object we want to build it as 'Dell XPS | 11.22 | DELL'
 * , declare <code>queryFormat = '{name} | {price} | {brand}'</code>.</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DocumentMetadata {

    /**
     * Specifies the format of the query used for vector searches.
     *
     * @return the query format.
     */
    String queryFormat();

    /**
     * Specifies the formatter for embedding content will be stored in vector db.
     *
     * @return the embedding content formatter.
     */
    String embeddingContentFormatter();

}
