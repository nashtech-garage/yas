package com.yas.search.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;
import java.util.List;

@Document(indexName = "product")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "case_insensitive")
    private String name;

    @Field(type = FieldType.Text)
    private String slug;

    @Field(type = FieldType.Double)
    private Double price;

    private Boolean isPublished;

    private Boolean isVisibleIndividually;

    private Boolean isAllowedToOrder;

    private Boolean isFeatured;

    private Long thumbnailMediaId;

    private Boolean isActive;

    @Field(type = FieldType.Keyword, analyzer = "case_insensitive")
    private String brand;

    @Field(type = FieldType.Keyword, analyzer = "case_insensitive")
    private List<String> categories;

    @Field(type = FieldType.Keyword)
    private List<String> attributes;

    @Field(type = FieldType.Date)
    private ZonedDateTime createdOn;
}
