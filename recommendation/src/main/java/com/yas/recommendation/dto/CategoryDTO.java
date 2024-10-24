package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class CategoryDTO extends BaseMetaDataEntity {
    private Long id;
    private String name;

    public CategoryDTO(MetaData metaData, Long id, String name) {
        super(metaData);
        this.id = id;
        this.name = name;
    }
}
