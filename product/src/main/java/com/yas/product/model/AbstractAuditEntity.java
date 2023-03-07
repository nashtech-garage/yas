package com.yas.product.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
public class AbstractAuditEntity {
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @CreatedBy
    private String createdBy;

    private ZonedDateTime lastModifiedOn = ZonedDateTime.now();

    @LastModifiedBy
    private String lastModifiedBy;
}
