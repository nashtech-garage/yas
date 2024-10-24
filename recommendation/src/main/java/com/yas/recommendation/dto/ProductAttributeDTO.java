package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductAttributeDTO extends BaseMetaDataEntity {
    private Long id;
    private String name;
    private String value;

    public ProductAttributeDTO(MetaData metaData, Long id, String name, String value) {
        super(metaData);
        this.id = id;
        this.name = name;
        this.value = value;
    }
}
