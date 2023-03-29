package com.yas.search.document;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "product_attribute_group")
@Builder
public class ProductAttributeGroup {
    @Id
    private Long id;

    private String name;
}
