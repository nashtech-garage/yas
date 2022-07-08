package com.yas.product.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
public class AbstractAuditEntity {
    private ZonedDateTime createdOn = ZonedDateTime.now();

    private String createdBy;

    private ZonedDateTime lastModifiedOn = ZonedDateTime.now();

    private String lastModifiedBy;
}
