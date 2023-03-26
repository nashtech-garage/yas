package com.yas.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "product")
public class Brand {
    @Id
    private Long id;

    private String name;

    private String slug;

    private Boolean isPublished;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Product> products = new ArrayList<>();
}
