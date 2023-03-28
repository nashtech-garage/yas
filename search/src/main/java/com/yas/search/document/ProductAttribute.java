package com.yas.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "product_attribute")
public class ProductAttribute {
    @Id
    private Long id;

    private String name;

    @Field(type = FieldType.Object)
    private ProductAttributeGroup productAttributeGroup;
}
