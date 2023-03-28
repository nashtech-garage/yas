package com.yas.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "category")
public class Category {
    @Id
    private Long id;

    private String name;

    private String description;

    private String slug;

    private String metaKeyword;

    private String metaDescription;

    private Short displayOrder;

    private Boolean isPublished;
}
