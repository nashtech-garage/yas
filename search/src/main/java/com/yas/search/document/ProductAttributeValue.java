package com.yas.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product")
public class ProductAttributeValue {
    @Id
    private Long id;

    private String value;

    @Field(type = FieldType.Object)
    private ProductAttribute productAttribute;
}
