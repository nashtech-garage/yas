package com.yas.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BaseMetaDataDto serves as an abstract base class for Data Transfer Objects (DTOs)
 * that include metadata fields for operation type and timestamp. This metadata helps
 * track changes or events associated with the entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public abstract class BaseMetaDataDto {
    protected String op;
    protected Long ts = System.currentTimeMillis();
}
