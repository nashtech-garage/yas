package com.yas.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "product_attribute_group")
public class ProductAttributeGroup {
    @Id
    private Long id;

    private String name;
}
