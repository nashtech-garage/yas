package com.yas.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * BaseDto is an abstract base class extending {@link BaseMetaDataDto} that represents
 * entities with a unique identifier and name. It provides constructors for initializing
 * metadata and basic entity properties.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public abstract class BaseDto extends BaseMetaDataDto {
    protected Long id;
    protected String name;

    /**
     * Constructs a BaseDto instance with operation type, timestamp, ID, and name.
     *
     * @param op   the operation type (e.g., CREATE, UPDATE, DELETE)
     * @param ts   the timestamp of the operation
     * @param id   the unique identifier for the entity
     * @param name the name associated with the entity
     */
    protected BaseDto(String op, Long ts, Long id, String name) {
        super(op, ts);
        this.id = id;
        this.name = name;
    }
}
