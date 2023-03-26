package com.yas.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseDocument {
    @Id
    private Long id;

    private String name;

    private String slug;

    private Double price;

    private Boolean isPublished;

    private Boolean isVisibleIndividually;

    private Long thumbnailMediaId;

    protected Boolean isActive;

    @Field(type = FieldType.Nested)
    private Brand brand;

    @Field(type = FieldType.Nested)
    private List<ProductCategory> productCategories = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<ProductAttributeValue> attributeValues = new ArrayList<>();
}
