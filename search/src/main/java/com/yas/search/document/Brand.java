package com.yas.search.document;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "brand")
@Builder
public class Brand {
    @Id
    private Long id;

    private String name;

    private String slug;

    private Boolean isPublished;
}
