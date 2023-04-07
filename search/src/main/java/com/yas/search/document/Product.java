package com.yas.search.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.ZonedDateTime;
import java.util.List;

@Document(indexName = "product")
@Setting(settingPath = "esconfig/elastic-analyzer.json")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
    private String name;

    private String slug;

    @Field(type = FieldType.Double)
    private Double price;

    private Boolean isPublished;

    private Boolean isVisibleIndividually;

    private Boolean isAllowedToOrder;

    private Boolean isFeatured;

    private Long thumbnailMediaId;

    private Boolean isActive;

    @Field(type = FieldType.Text)
    private String brand;

    @Field(type = FieldType.Keyword)
    private List<String> categories;

    @Field(type = FieldType.Keyword)
    private List<String> attributes;

    @Field(type = FieldType.Date)
    private ZonedDateTime createdOn;
}
