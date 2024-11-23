package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * AggregationDto is a generic container class for aggregating a set of items of type T.
 * It includes a join ID for association with other data entities and supports adding
 * individual items to the aggregation set.
 *
 * @param <T> the type of elements to aggregate
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@SuppressWarnings("java:S1068")
public class AggregationDto<T> {
    private Long joinId;
    private Set<T> aggregationContents;

    public AggregationDto() {
        aggregationContents = new HashSet<>();
    }

    public void add(T aggregationContent) {
        aggregationContents.add(aggregationContent);
    }
}
