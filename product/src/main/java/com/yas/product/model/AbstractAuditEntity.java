package com.yas.product.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
public class AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime createdOn = ZonedDateTime.now();

    private String createdBy;

    private ZonedDateTime lastModifiedOn = ZonedDateTime.now();

    private String lastModifiedBy;
}
