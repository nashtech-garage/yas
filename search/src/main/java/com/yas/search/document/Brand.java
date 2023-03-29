package com.yas.search.document;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "brand")
@Builder
public class Brand {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    private String slug;

    private Boolean isPublished;
}
