package com.yas.search.document;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product_attribute_value")
@Builder
public class ProductAttributeValue {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String value;

    @Field(type = FieldType.Object)
    private ProductAttribute productAttribute;
}
