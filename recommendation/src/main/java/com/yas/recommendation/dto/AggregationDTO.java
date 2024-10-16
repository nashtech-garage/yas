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
public class AggregationDTO<ID, T> {
    private ID joinId;
    private Set<T> aggregationContents;

    public AggregationDTO() {
        aggregationContents = new HashSet<>();
    }

    public void add(T aggregationContent) {
        aggregationContents.add(aggregationContent);
    }

}
