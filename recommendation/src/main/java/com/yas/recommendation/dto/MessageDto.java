package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MessageDto is a generic Data Transfer Object (DTO) used to represent a change data capture (CDC) message.
 * It extends {@link BaseDto} to include metadata and entity identification, and includes fields for
 * both the "before" and "after" states of an entity, facilitating change tracking.
 *
 * @param <T> the type of the entity being captured in the message
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public class MessageDto<T> extends BaseMetaDataDto {
    private T before;
    private T after;
}
