package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * KeyDto is a Data Transfer Object (DTO) representing the unique identifier (ID) of an entity.
 * It is commonly used as the key in message processing contexts.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public class KeyDto {
    private Long id;
}
