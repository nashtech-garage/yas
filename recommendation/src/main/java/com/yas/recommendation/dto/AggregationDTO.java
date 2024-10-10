package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
public class AggregationDTO<T, E> {
    private T targetId;
    private Set<E> aggregationContents;

    public AggregationDTO() {
        aggregationContents = new HashSet<>();
    }

    public void add(E aggregationContent) {
        aggregationContents.add(aggregationContent);
    }

}
