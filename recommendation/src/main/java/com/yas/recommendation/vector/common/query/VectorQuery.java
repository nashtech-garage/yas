package com.yas.recommendation.vector.common.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract service for performing similarity searches on a vector store.
 *
 * @param <D> the document type, must be annotated with {@link DocumentMetadata},
 *            to know how the query will be formatted
 * @param <P> the product result type handled by the service.
 */
public abstract class VectorQuery<D extends BaseDocument, P> {

    private ObjectMapper objectMapper;
    private JdbcVectorService jdbcVectorService;

    @Getter
    private final Class<D> docType;

    @Getter
    private final Class<P> resultType;

    /**
     * Constructor for VectorQueryService.
     *
     * @param docType    the document class type.
     * @param resultType the result class type.
     */
    @SneakyThrows
    protected VectorQuery(Class<D> docType, Class<P> resultType) {
        this.docType = docType;
        this.resultType = resultType;
    }

    /**
     * Performs a similarity search based on a product ID stored in db.
     *
     * @param id the ID of the product for which to perform the similarity search.
     * @return a list of product results that are similar to the specified product in db,
     * result will not include specified product.
     */
    public List<P> similaritySearch(Long id) {
        return toResult(jdbcVectorService.similarityProduct(id, this.getDocType()));
    }

    /**
     * Converts a list of documents to a list of product results.
     *
     * @param documents the list of documents to convert.
     * @return a list of product results corresponding to the provided documents.
     */
    protected List<P> toResult(List<Document> documents) {
        return documents
            .stream()
            .filter(doc -> doc.getMetadata() != null)
            .map(doc -> objectMapper.convertValue(doc.getMetadata(), resultType))
            .toList();
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setJdbcVectorService(JdbcVectorService jdbcVectorService) {
        this.jdbcVectorService = jdbcVectorService;
    }
}
