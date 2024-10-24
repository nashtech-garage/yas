package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductAttributeValueDTO extends BaseMetaDataEntity {
    private Long id;
    private String value;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_attribute_id")
    private Long productAttributeId;
    private String productAttributeName;
    private boolean isDeleted;

    public ProductAttributeValueDTO(Long id) {
        this.id = id;
    }
}
