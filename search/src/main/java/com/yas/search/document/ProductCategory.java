package com.yas.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product_category")
public class ProductCategory {
    @Id
    private Long id;

    @Field(type = FieldType.Object)
    private Category category;
}
