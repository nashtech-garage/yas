package com.yas.search.document;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "category")
@Builder
public class Category {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    private String description;

    private String slug;

    private String metaKeyword;

    private String metaDescription;

    private Short displayOrder;

    private Boolean isPublished;
}
