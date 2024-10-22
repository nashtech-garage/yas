package com.yas.recommendation.store;

import static com.yas.recommendation.vector.common.store.SimpleVectorRepository.TYPE_METADATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DocumentFormatter;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseVectorRepositoryTest<D extends BaseDocument, E> {

    private final Class<D> docClass;
    private final DocumentMetadata documentMetadata;

    @Getter
    private final DocumentFormatter documentFormatter;

    @Getter
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddingSearchConfiguration embeddingSearchConf;

    @SneakyThrows
    public BaseVectorRepositoryTest(Class<D> docClass) {
        Assertions.assertNotNull(docClass, "Document must not be 'null'");
        this.docClass = docClass;
        this.documentMetadata = getDocumentMetadata();
        this.documentFormatter = documentMetadata
            .documentFormatter()
            .getDeclaredConstructor()
            .newInstance();
    }

    public DocumentMetadata getDocumentMetadata() {
       assertTrue(
           docClass.isAnnotationPresent(DocumentMetadata.class),
           "Document must be annotated by 'DocumentMetadata'"
       );
        return docClass.getAnnotation(DocumentMetadata.class);
    }

    public void assertDocumentData(Document createdDoc, E entity) {
        var expectedContent = getFormatEntity(entity);
        assertEquals(expectedContent, createdDoc.getContent(), "Document format must be formated at declared metadata");
        assertNotNull(createdDoc.getMetadata(), "Document's metadata must not be null");
        assertFalse(createdDoc.getMetadata().isEmpty(), "Document's metadata must not be empty");

        var expectedMetadata = objectMapper.convertValue(entity, Map.class);
        expectedMetadata.put(TYPE_METADATA, documentMetadata.docIdPrefix());
        assertEquals(expectedMetadata.keySet(), createdDoc.getMetadata().keySet());
    }

    public void assertSearchRequest(SearchRequest searchRequest, E entity) {
        assertNotNull(searchRequest.query, "Search query must be created");
        assertEquals(getFormatEntity(entity), searchRequest.query, "Search's Query must be formatted correctly");
        assertEquals(searchRequest.getTopK(), embeddingSearchConf.topK(), "Search's top K must be configured");
        assertEquals(
            searchRequest.getSimilarityThreshold(),
            embeddingSearchConf.similarityThreshold(),
            "Search's top K must be configured"
        );
        assertNotNull(searchRequest.getFilterExpression(), "Search filter default must be specified");
        assertEquals(
            searchRequest.getFilterExpression().type(),
            Filter.ExpressionType.NE,
            "Search filter default must be correctly"
        );

        Filter.Key key = (Filter.Key) searchRequest.getFilterExpression().left();
        assertEquals(key.key(), "id", "Search filter default must be correctly");
    }

    private String getFormatEntity(E entity) {
        return documentFormatter.format(
            objectMapper.convertValue(entity, Map.class),
            documentMetadata.contentFormat(),
            objectMapper
        );
    }

}
