package com.yas.recommendation.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private String metaKeyword;
    private String metaDescription;
    private Short displayOrder;
    private Boolean isPublished;
}
