package com.yas.recommendation.startup;

import static com.yas.recommendation.vector.document.ProductDocument.CUSTOM_CONTENT_FORMATTER;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
class DataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    @Value("classpath:/data/laptop.json")
    private Resource laptopResource;

    private final VectorStore vectorStore;

    public DataLoaderService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void load() {
        JsonReader jsonReader = new JsonReader(
                laptopResource,
                new ProductMetaDataGenerator(),
            "description",
            "has_options",
            "meta_description",
            "meta_keyword",
            "meta_title",
            "name",
            "price",
            "short_description",
            "specification"
        );
        vectorStore.add(
            jsonReader
                .get()
                .stream()
                .peek(doc -> doc.setContentFormatter(CUSTOM_CONTENT_FORMATTER))
                .toList()
        );
        logger.info("Load data: %s.".formatted(laptopResource.getFilename()));
    }
}

class ProductMetaDataGenerator implements JsonMetadataGenerator {

    @Override
    public Map<String, Object> generate(Map<String, Object> jsonMap) {
        return jsonMap;
    }
}

